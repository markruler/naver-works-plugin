package io.jenkins.plugins.naverworks.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.naverworks.RuntimeExceptionWrapper;
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
import io.jenkins.plugins.naverworks.UserConfiguration;
import org.apache.commons.lang.StringUtils;
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

    @Override
    public Message write(UserConfiguration parameter) {

        final int maxListTemplateElements = 4;

        final List<Map<String, String>> messages = parameter.getMessages();
        final String backgroundImageUrl = parameter.getBackgroundImageUrl();
        final String contentActionLabel = parameter.getContentActionLabel();
        final String contentActionLink = parameter.getContentActionLink();
        String notification = parameter.getNotification();

        if (messages == null || messages.isEmpty()) {
            if (StringUtils.isBlank(notification)) {
                notification = "Changes have been deployed.";
            }
            LinkContent content =
                    new LinkContent(
                            notification,
                            contentActionLabel,
                            contentActionLink
                    );
            return new LinkMessage(content);
        }

        if (messages.size() == 1 && StringUtils.isBlank(backgroundImageUrl)) {
            LinkContent content =
                    new LinkContent(
                            notification,
                            contentActionLabel,
                            contentActionLink
                    );
            return new LinkMessage(content);
        }

        if (messages.size() <= maxListTemplateElements) {
            ListTemplateContent content = new ListTemplateContent();
            content.setCoverData(new CoverData(backgroundImageUrl));
            content.setMessages(messages);
            Action action = new Action("uri", contentActionLabel, contentActionLink);
            content.setActions(Collections.singletonList(action));

            return new ListTemplateMessage(content);
        }

        CarouselContent content = new CarouselContent();
        content.setMessages(messages, backgroundImageUrl, contentActionLink);
        return new CarouselMessage(content);
    }

    public String send(final Token token, final Bot bot, final Message message) {

        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final String CHANNEL_BOT_API =
                    String.format(
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
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeExceptionWrapper(e);
        }
    }

}
