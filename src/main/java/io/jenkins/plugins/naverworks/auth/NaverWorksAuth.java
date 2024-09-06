package io.jenkins.plugins.naverworks.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.naverworks.RuntimeExceptionWrapper;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NAVER Works 인증 요청
 */
public class NaverWorksAuth {

    private static final Logger LOG = Logger.getLogger(NaverWorksAuth.class.getName());

    public static final String AUTH_API = "https://auth.worksmobile.com/oauth2/v2.0/token";

    /**
     * 서비스 계정(SA, Service Account)으로 인증 토큰을 요청한다.
     *
     * @return NAVER Works Token
     * @see <a href="https://developers.worksmobile.com/docs/auth-jwt">서비스 계정으로 인증(JWT)</a>
     */
    public Token requestNaverWorksToken(final NaverWorksCredential credential) {

        // Lookup NAVER Works Token
        final Token token = credential.getToken();
        if (token != null) {
            LOG.log(Level.INFO, "{0} Token exists.", token.getTokenType());

            final long expired = token.getExpired();
            final long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+9"));
            if (expired > now) {
                LOG.log(Level.INFO, "Stored token is not expired.");
                return token;
            }
        }

        // Request New Token
        try {
            return saveNewToken(credential);
        } catch (Exception e) {
            throw new RuntimeExceptionWrapper(e);
        }
    }

    /**
     * 새로운 인증 토큰을 발급 받아서 Credential에 저장한다.
     *
     * @param credential NAVER Works Credential
     * @return 새로운 인증 토큰
     */
    private Token saveNewToken(NaverWorksCredential credential)
            throws GeneralSecurityException, IOException, URISyntaxException {

        // 토큰 발급
        Token token = requestNewToken(credential);

        // Credential 저장
        credential.setToken(token);
        SystemCredentialsProvider provider = SystemCredentialsProvider.getInstance();
        provider.save();

        return token;
    }

    /**
     * 새로운 인증 토큰을 발급하기 위해 NAVER Works에 인증을 요청한다.
     *
     * @param credential NAVER Works Credential
     * @return 인증 토큰
     */
    private Token requestNewToken(NaverWorksCredential credential)
            throws GeneralSecurityException, IOException, URISyntaxException {

        final String assertion = generateJwtWithServiceAccount(credential);
        final String encodedGrantType = URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", StandardCharsets.UTF_8.name());

        final List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("assertion", assertion));
        params.add(new BasicNameValuePair("grant_type", encodedGrantType));
        params.add(new BasicNameValuePair("client_id", credential.getClientId()));
        params.add(new BasicNameValuePair("client_secret", credential.getClientSecret()));
        params.add(new BasicNameValuePair("scope", "bot"));

        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            LOG.log(Level.INFO, "Request new token.");

            URI uri = new URI(AUTH_API);
            HttpPost httpRequest = new HttpPost(uri);
            httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpRequest.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            String httpResponse = httpClient.execute(httpRequest, new NaverWorksResponseHandler());
            final ObjectMapper objectMapper = new ObjectMapper();
            Token token = objectMapper.readValue(httpResponse, Token.class);
            // TODO: 보안을 위해 1시간으로 제한했지만, 파라미터로 받는 것이 좋을 듯함.
            LocalDateTime expired = LocalDateTime.now().plusHours(1);
            token.setExpired(expired.toEpochSecond(ZoneOffset.of("+9")));

            return token;
        }
    }

    /**
     * 요청 JWT를 생성한다.
     *
     * @param credential NAVER Works Credential
     * @return JWT
     * @throws GeneralSecurityException
     * @see <a href="https://developers.worksmobile.com/kr/docs/auth-jwt#generate-jwt">JWT 생성</a>
     */
    public String generateJwtWithServiceAccount(NaverWorksCredential credential)
            throws GeneralSecurityException {

        // header
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");

        // date
        Date iat = new Date();
        Date exp = DateUtils.addMinutes(new Date(), 30);

        // JWT 전자 서명 (RFC-7515)
        RSAPrivateKey privateKey = RSA.getPrivateKey(credential.getPrivateKey());
        Algorithm algorithmRS = Algorithm.RSA256(null, privateKey);

        return JWT.create()
                .withHeader(headers)
                .withIssuer(credential.getClientId())
                .withSubject(credential.getServiceAccount())
                .withIssuedAt(iat)
                .withExpiresAt(exp)
                .sign(algorithmRS);
    }
}
