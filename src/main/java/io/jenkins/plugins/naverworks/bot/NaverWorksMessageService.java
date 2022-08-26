package io.jenkins.plugins.naverworks.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.naverworks.auth.Token;
import io.jenkins.plugins.naverworks.bot.message.Message;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * 메시지를 Bot에 보낸다.
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-channel-message-send">메시지 전송 - 채널 대상</a>
 */
public class NaverWorksMessageService implements MessageService {

    private static final String APPLICATION_JSON = "application/json";

    public String sendMessage(final Token token, final Bot bot, final Message message)
            throws URISyntaxException, IOException {

        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = new URI(String.format("https://www.worksapis.com/v1.0/bots/%s/channels/%s/messages", bot.getId(), bot.getChannelId()));

            HttpPost httpRequest = new HttpPost(uri);
            httpRequest.addHeader("Authorization", "Bearer " + token.getAccessToken());
            httpRequest.addHeader("Accept", APPLICATION_JSON);
            httpRequest.addHeader("Content-Type", APPLICATION_JSON);

            // Message
            final ObjectMapper objectMapper = new ObjectMapper();
            final String messageJSON = objectMapper.writeValueAsString(message);
            final StringEntity requestEntity = new StringEntity(messageJSON, StandardCharsets.UTF_8);
            httpRequest.setEntity(requestEntity);

            final HttpClientResponseHandler<String> responseHandler = response -> {
                final int status = response.getCode();
                if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                    final HttpEntity responseEntity = response.getEntity();
                    try {
                        if (responseEntity != null) {
                            return EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                        }
                        return null;
                    } catch (final ParseException ex) {
                        throw new ClientProtocolException(ex);
                    }
                } else {
                    throw new ClientProtocolException(
                            String.format(
                                    "Unexpected response status - %d:%s",
                                    status,
                                    EntityUtils.toString(response.getEntity())
                            )
                    );
                }
            };
            return httpClient.execute(httpRequest, responseHandler);
        }
    }

}
