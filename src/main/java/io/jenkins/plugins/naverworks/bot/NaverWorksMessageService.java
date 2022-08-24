package io.jenkins.plugins.naverworks.bot;

import io.jenkins.plugins.naverworks.auth.Token;

import java.io.IOException;
import java.net.URISyntaxException;

public interface NaverWorksMessageService {

    /**
     * 메세지를 전송한다.
     *
     * @param token   NAVER Works Token
     * @param bot     NAVER Works 메신저 Bot
     * @param message 전송할 메시지
     * @return API 응답 메시지
     */
    String sendMessage(final Token token, final Bot bot, final Message message)
            throws URISyntaxException, IOException;
}
