package io.jenkins.plugins.naverworks.bot;

import io.jenkins.plugins.naverworks.auth.Token;
import io.jenkins.plugins.naverworks.bot.message.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {

    /**
     * 메시지를 작성한다.
     *
     * @param messages           내용
     * @param backgroundImageUrl 커버 이미지
     * @param contentActionLabel 메시지 레이블
     * @param contentActionLink  메시지 레이블을 눌렀을 때 이동하는 URL
     * @return 메시지
     */
    Message write(List<Map<String, String>> messages, String backgroundImageUrl, String contentActionLabel, String contentActionLink);

    /**
     * 메세지를 전송한다.
     *
     * @param token   NAVER Works Token
     * @param bot     NAVER Works 메신저 Bot
     * @param message 전송할 메시지
     * @return API 응답 메시지
     */
    String send(final Token token, final Bot bot, final Message message);
}
