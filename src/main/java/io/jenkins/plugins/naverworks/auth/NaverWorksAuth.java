package io.jenkins.plugins.naverworks.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.naverworks.App;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NAVER Works 인증 요청기
 * <p>
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/authorization-sa">서비스 계정으로 인증(JWT)</a>
 */
public class NaverWorksAuth {

    public static final String AUTH_API = "https://auth.worksmobile.com/oauth2/v2.0/token";

    /**
     * 서비스 어카운트를 통해 토큰을 요청한다.
     * *
     *
     * @return NAVER Works Token
     * @throws URISyntaxException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public Token requestNaverWorksToken(final App app)
            throws URISyntaxException, IOException, GeneralSecurityException {

        final String assertion = generateJwtWithServiceAccount(app);
        final String encodedGrantType = URLEncoder.encode(
                "urn:ietf:params:oauth:grant-type:jwt-bearer",
                StandardCharsets.UTF_8.name());

        final List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("assertion", assertion));
        params.add(new BasicNameValuePair("grant_type", encodedGrantType));
        params.add(new BasicNameValuePair("client_id", app.getClientId()));
        params.add(new BasicNameValuePair("client_secret", app.getClientSecret()));
        params.add(new BasicNameValuePair("scope", "bot"));

        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = new URI(AUTH_API);
            HttpPost httpRequest = new HttpPost(uri);
            httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpRequest.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            String httpResponse = httpClient.execute(httpRequest, new NaverWorksResponseHandler());
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(httpResponse, Token.class);
        }
    }

    /**
     * 요청 JWT를 생성한다.
     *
     * @return JWT
     * @throws GeneralSecurityException
     */
    public String generateJwtWithServiceAccount(App app)
            throws GeneralSecurityException {

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");
        Date iat = new Date();
        Date exp = DateUtils.addMinutes(new Date(), 30);

        // JWT 전자 서명 (RFC-7515)
        RSAPrivateKey privateKey = RSA.getPrivateKey(app.getPrivateKey());
        Algorithm algorithmRS = Algorithm.RSA256(null, privateKey);

        return JWT.create()
                .withHeader(headers)
                .withIssuer(app.getClientId())
                .withSubject(app.getServiceAccount())
                .withIssuedAt(iat)
                .withExpiresAt(exp)
                .sign(algorithmRS);
    }
}
