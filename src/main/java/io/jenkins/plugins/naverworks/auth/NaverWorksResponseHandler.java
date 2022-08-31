package io.jenkins.plugins.naverworks.auth;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NaverWorksResponseHandler
        implements HttpClientResponseHandler<String> {

    private static final Logger LOG = Logger.getLogger(NaverWorksResponseHandler.class.getName());

    @Override
    public String handleResponse(ClassicHttpResponse response)
            throws HttpException, IOException {

        // https://developers.worksmobile.com/kr/reference/rate-limits
        // bot API 240 requests/min
        // RateLimit-Limit      기준 시간 단위 API 호출 수
        // RateLimit-Remaining  기준 시간 단위 남은 API 호출 수
        // RateLimit-Reset      기준 시간 갱신까지 남은 시간(초)
        final Header[] headers = response.getHeaders();
        for (Header header : headers) {
            LOG.log(Level.FINE, "{0}: {1}", new String[]{header.getName(), header.getValue()});
        }

        final int status = response.getCode();
        final HttpEntity httpEntity = response.getEntity();
        final String entity = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);

        if (HttpStatus.SC_SUCCESS <= status && status < HttpStatus.SC_REDIRECTION) {
            return entity;
        }

        if (status == HttpStatus.SC_FORBIDDEN) {
            return String.valueOf(status);
        }

        String unexpected = String.format("Unexpected response status - %d:%s", status, entity);
        throw new ClientProtocolException(unexpected);
    }

}
