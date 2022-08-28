package io.jenkins.plugins.naverworks.auth;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NaverWorksResponseHandler
        implements HttpClientResponseHandler<String> {

    @Override
    public String handleResponse(ClassicHttpResponse response)
            throws HttpException, IOException {

        final int status = response.getCode();

        if (status >= HttpStatus.SC_SUCCESS
                && status < HttpStatus.SC_REDIRECTION) {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            }
            return null;
        }

        String responseMessage = String.format(
                "Unexpected response status - %d:%s",
                status,
                EntityUtils.toString(response.getEntity())
        );
        throw new ClientProtocolException(responseMessage);
    }

}
