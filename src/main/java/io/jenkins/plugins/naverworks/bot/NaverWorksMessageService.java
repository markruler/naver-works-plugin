package io.jenkins.plugins.naverworks.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.naverworks.RuntimeExceptionWrapper;
import io.jenkins.plugins.naverworks.UserConfiguration;
import io.jenkins.plugins.naverworks.auth.NaverWorksResponseHandler;
import io.jenkins.plugins.naverworks.auth.Token;
import io.jenkins.plugins.naverworks.bot.message.Message;
import io.jenkins.plugins.naverworks.bot.message.carousel.CarouselContent;
import io.jenkins.plugins.naverworks.bot.message.carousel.CarouselMessage;
import io.jenkins.plugins.naverworks.bot.message.link.LinkContent;
import io.jenkins.plugins.naverworks.bot.message.link.LinkMessage;
import io.jenkins.plugins.naverworks.bot.message.list.ListTemplateContent;
import io.jenkins.plugins.naverworks.bot.message.list.ListTemplateMessage;
import io.jenkins.plugins.naverworks.bot.message.text.TextContent;
import io.jenkins.plugins.naverworks.bot.message.text.TextMessage;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * 메시지를 Bot에 보낸다.
 */
public class NaverWorksMessageService implements MessageService {

    private static final String APPLICATION_JSON = "application/json";

    public static final String BOT_API = "https://www.worksapis.com/v1.0/bots";

    @Override
    public Message write(UserConfiguration userConfiguration) {
        final String type = userConfiguration.getMessageType();
        if (isBlank(type)) {
            throw new MessageTypeNotFound("messageType is empty");
        }
        return writeTemplateMessage(userConfiguration);
    }

    /**
     * type별 메시지를 만든다.
     *
     * @param userConfiguration 사용자 설정
     * @return 메시지
     */
    private Message writeTemplateMessage(UserConfiguration userConfiguration) {

        final List<Map<String, String>> messages = userConfiguration.getMessages();
        final String backgroundImageUrl = userConfiguration.getBackgroundImageUrl();
        final String contentActionLabel = userConfiguration.getContentActionLabel();
        final String contentActionLink = userConfiguration.getContentActionLink();
        String messageType = userConfiguration.getMessageType();
        String notification = userConfiguration.getNotification();

        if (isBlank(notification)) {
            notification = "Changes have been deployed.";
        }

        switch (messageType) {

            case TextContent.TYPE:
                TextContent textContent = new TextContent(notification);
                return new TextMessage(textContent);

            case LinkContent.TYPE:
                LinkContent linkContent = new LinkContent(notification, contentActionLabel, contentActionLink);
                return new LinkMessage(linkContent);

            case ListTemplateContent.TYPE:
                ListTemplateContent listTemplateContent = new ListTemplateContent();
                listTemplateContent.setMessages(messages, backgroundImageUrl, contentActionLabel, contentActionLink);
                return new ListTemplateMessage(listTemplateContent);

            case CarouselContent.TYPE:
                CarouselContent carouselContent = new CarouselContent();
                carouselContent.setMessages(messages, backgroundImageUrl, contentActionLink);
                return new CarouselMessage(carouselContent);

            default:
                throw new MessageTypeNotFound("messageType not found");
        }
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
