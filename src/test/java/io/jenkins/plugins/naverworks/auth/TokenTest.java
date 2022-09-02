package io.jenkins.plugins.naverworks.auth;

import org.javaunit.autoparams.AutoSource;
import org.javaunit.autoparams.Repeat;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

    @ParameterizedTest
    @AutoSource
    @Repeat(5)
    void encrypt_and_decrypt_naver_works_token(final String accessToken, final String refreshToken) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);

        assertThat(token.getAccessToken()).isEqualTo(accessToken);
        assertThat(token.getRefreshToken()).isEqualTo(refreshToken);
    }

}
