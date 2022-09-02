package io.jenkins.plugins.naverworks.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hudson.util.Secret;

import java.io.Serializable;

/**
 * NAVER Works Token
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Token implements Serializable {

    private static final long serialVersionUID = 2L;

    private Secret accessToken;
    private Secret refreshToken;
    private String scope;
    private String tokenType;
    private int expiresIn;
    private long expired;

    public String getAccessToken() {
        return Secret.toString(accessToken);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = Secret.fromString(accessToken);
    }

    public String getRefreshToken() {
        return Secret.toString(refreshToken);
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = Secret.fromString(refreshToken);
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }
}
