package io.jenkins.plugins.naverworks;

import io.jenkins.plugins.naverworks.auth.NaverWorksAuth;
import io.jenkins.plugins.naverworks.bot.NaverWorksMessageService;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ProtocolException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

class NaverWorksApiTest {

    private final String headerAllowHttpMethod = "allow";

    enum HttpMethod {
        POST,
        GET,
        OPTIONS
    }

    @Test
    @Disabled("Live Test")
    void validate_auth_interface() throws URISyntaxException, IOException, ProtocolException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URI uri = new URI(NaverWorksAuth.AUTH_API);
        HttpOptions request = new HttpOptions(uri);

        CloseableHttpResponse response = httpClient.execute(request);

        Header allow = response.getHeader(headerAllowHttpMethod);
        String methods = allow.getValue();

        assertThat(methods)
                .containsOnlyOnce(HttpMethod.POST.name())
                .containsOnlyOnce(HttpMethod.OPTIONS.name());
    }

    @Test
    @Disabled("Live Test")
    void validate_message_bot_interface() throws URISyntaxException, IOException, ProtocolException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URI uri = new URI(NaverWorksMessageService.BOT_API);
        HttpOptions request = new HttpOptions(uri);

        CloseableHttpResponse response = httpClient.execute(request);

        Header allow = response.getHeader(headerAllowHttpMethod);
        String methods = allow.getValue();

        assertThat(methods)
                .containsOnlyOnce(HttpMethod.POST.name())
                .containsOnlyOnce(HttpMethod.OPTIONS.name());
    }

}
