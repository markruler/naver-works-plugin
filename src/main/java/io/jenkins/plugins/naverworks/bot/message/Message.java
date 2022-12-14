package io.jenkins.plugins.naverworks.bot.message;

/**
 * 공통 메시지
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-content">메시지 공통 속성</a>
 */
public interface Message {

    /**
     * 메시지 내용을 조회한다.
     *
     * @return 메시지 내용
     */
    Content getContent();

}
