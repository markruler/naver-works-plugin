package io.jenkins.plugins.naverworks.bot;

import io.jenkins.plugins.naverworks.auth.Token;
import io.jenkins.plugins.naverworks.bot.message.Message;
import io.jenkins.plugins.naverworks.UserConfiguration;

public interface MessageService {

    /**
     * 메시지를 작성한다.
     *
     * @param userConfiguration 사용자 설정
     * @return 메시지
     */
    Message write(UserConfiguration userConfiguration);

    /**
     * 메세지를 전송한다.
     *
     * @param token   NAVER Works Token
     * @param bot     NAVER Works 메신저 Bot
     * @param message 전송할 메시지
     * @return API 응답 메시지
     * @see <a href="https://developers.worksmobile.com/kr/reference/bot-channel-message-send?lang=ko">메시지 전송 - 채널 대상</a>
     */
    String send(final Token token, final Bot bot, final Message message);
}
