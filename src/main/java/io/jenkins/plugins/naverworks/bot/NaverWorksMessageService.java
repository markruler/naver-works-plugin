package io.jenkins.plugins.naverworks.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.naverworks.auth.NaverWorksResponseHandler;
import io.jenkins.plugins.naverworks.auth.Token;
import io.jenkins.plugins.naverworks.bot.message.Action;
import io.jenkins.plugins.naverworks.bot.message.CarouselContent;
import io.jenkins.plugins.naverworks.bot.message.CarouselMessage;
import io.jenkins.plugins.naverworks.bot.message.CoverData;
import io.jenkins.plugins.naverworks.bot.message.LinkContent;
import io.jenkins.plugins.naverworks.bot.message.LinkMessage;
import io.jenkins.plugins.naverworks.bot.message.ListTemplateContent;
import io.jenkins.plugins.naverworks.bot.message.ListTemplateMessage;
import io.jenkins.plugins.naverworks.bot.message.Message;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 메시지를 Bot에 보낸다.
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-channel-message-send">메시지 전송 - 채널 대상</a>
 */
public class NaverWorksMessageService implements MessageService {

    private static final String APPLICATION_JSON = "application/json";

    public static final String BOT_API = "https://www.worksapis.com/v1.0/bots";

    public String send(final Token token, final Bot bot, final Message message)
            throws URISyntaxException, IOException {

        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final String CHANNEL_BOT_API = String.format(
                    "%s/%s/channels/%s/messages",
                    BOT_API,
                    bot.getId(),
                    bot.getChannelId()
            );
            URI uri = new URI(CHANNEL_BOT_API);

            HttpPost httpRequest = new HttpPost(uri);
            httpRequest.addHeader("Authorization", "Bearer " + token.getAccessToken());
            httpRequest.addHeader("Accept", APPLICATION_JSON);
            httpRequest.addHeader("Content-Type", APPLICATION_JSON);

            final ObjectMapper objectMapper = new ObjectMapper();
            final String messageJSON = objectMapper.writeValueAsString(message);
            final StringEntity requestEntity = new StringEntity(messageJSON, StandardCharsets.UTF_8);
            httpRequest.setEntity(requestEntity);

            return httpClient.execute(httpRequest, new NaverWorksResponseHandler());
        }
    }

    @Override
    public Message write(
            List<Map<String, String>> messages,
            String backgroundImageUrl,
            String contentActionLabel,
            String contentActionLink
    ) {

        Message message;
        final int maxListTemplateElements = 4;

        if (messages == null || messages.isEmpty()) {
            LinkContent content = new LinkContent(
                    "Changes have been deployed.",
                    contentActionLabel,
                    contentActionLink
            );
            message = new LinkMessage(content);
        } else if (messages.size() <= maxListTemplateElements) {
            ListTemplateContent content = new ListTemplateContent();
            content.setCoverData(new CoverData(backgroundImageUrl));
            content.setMessages(messages);
            Action action = new Action("uri", contentActionLabel, contentActionLink);
            content.setActions(Collections.singletonList(action));

            message = new ListTemplateMessage(content);
        } else {
            CarouselContent content = new CarouselContent();
            content.setMessages(messages, backgroundImageUrl, contentActionLink);
            message = new CarouselMessage(content);
        }
        return message;
    }

}
