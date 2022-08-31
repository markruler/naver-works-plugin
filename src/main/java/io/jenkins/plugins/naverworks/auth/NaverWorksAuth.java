package io.jenkins.plugins.naverworks.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cloudbees.plugins.credentials.CredentialsMatcher;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.security.ACL;
import io.jenkins.plugins.naverworks.App;
import io.jenkins.plugins.naverworks.RuntimeExceptionWrapper;
import jenkins.model.Jenkins;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NAVER Works 인증 요청
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/authorization-sa">서비스 계정으로 인증(JWT)</a>
 */
public class NaverWorksAuth {

    private static final Logger LOG = Logger.getLogger(NaverWorksAuth.class.getName());

    public static final String AUTH_API = "https://auth.worksmobile.com/oauth2/v2.0/token";
    private static final String NAVER_WORKS_TOKEN_ID = "naver-works-token";

    /**
     * 서비스 어카운트를 통해 토큰을 요청한다.
     *
     * @return NAVER Works Token
     */
    public Token requestNaverWorksToken(final App app) {

        List<NaverWorksTokenCredentials> credentials =
                CredentialsProvider.lookupCredentials(
                        NaverWorksTokenCredentials.class,
                        Jenkins.get(),
                        ACL.SYSTEM,
                        Collections.emptyList()
                );

        // Lookup NAVER Works Token
        NaverWorksTokenCredentials storedCredential = lookupToken(credentials);
        if (storedCredential != null) {
            Token token = storedCredential.getToken();
            LOG.log(Level.INFO, "{0} Token exists.", token.getTokenType());

            final long expired = token.getExpired();
            final long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+9"));
            if (expired >= now) {
                LOG.log(Level.INFO, "Stored token is not expired.");
                return token;
            }
        }

        // Request New Token
        try {
            return saveNewToken(app, storedCredential);
        } catch (Exception e) {
            throw new RuntimeExceptionWrapper(e);
        }
    }

    /**
     * 새로운 토큰을 발급 받아서 저장한다.
     *
     * @param app              NAVER Works 앱
     * @param storedCredential 기존에 저장 중인 토큰
     * @return 새로운 토큰
     */
    private Token saveNewToken(App app, NaverWorksTokenCredentials storedCredential)
            throws GeneralSecurityException, IOException, URISyntaxException {

        Token token = requestNewToken(app);

        NaverWorksTokenCredentials newCredential =
                new NaverWorksTokenCredentials(
                        CredentialsScope.GLOBAL,
                        NAVER_WORKS_TOKEN_ID,
                        "NAVER Works Token",
                        token
                );

        CredentialsStore store = CredentialsProvider.lookupStores(Jenkins.get()).iterator().next();
        if (storedCredential != null) {
            store.updateCredentials(Domain.global(), storedCredential, newCredential);
        } else {
            store.addCredentials(Domain.global(), newCredential);
        }

        SystemCredentialsProvider provider = SystemCredentialsProvider.getInstance();
        provider.save();

        return token;
    }

    /**
     * Token 조회
     *
     * @param credentials NAVER Works Token Credentials 목록
     * @return id 값이 {@link NAVER_WORKS_TOKEN_ID}인 token
     */
    private NaverWorksTokenCredentials lookupToken(List<NaverWorksTokenCredentials> credentials) {
        final CredentialsMatcher matcher = CredentialsMatchers.withId(NAVER_WORKS_TOKEN_ID);
        return CredentialsMatchers.firstOrNull(credentials, matcher);
    }

    /**
     * 새로운 토큰 발급을 요청한다.
     *
     * @param app NAVER Works app
     * @return 인증 토큰
     */
    private Token requestNewToken(App app)
            throws GeneralSecurityException, IOException, URISyntaxException {

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
            LOG.log(Level.INFO, "Request new token.");

            URI uri = new URI(AUTH_API);
            HttpPost httpRequest = new HttpPost(uri);
            httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpRequest.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            String httpResponse = httpClient.execute(httpRequest, new NaverWorksResponseHandler());
            final ObjectMapper objectMapper = new ObjectMapper();
            Token token = objectMapper.readValue(httpResponse, Token.class);
            LocalDateTime expired = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
            token.setExpired(expired.toEpochSecond(ZoneOffset.of("+9")));

            return token;
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
