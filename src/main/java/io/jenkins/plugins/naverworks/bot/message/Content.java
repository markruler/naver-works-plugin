package io.jenkins.plugins.naverworks.bot.message;

/**
 * 메시지 내용
 */
public interface Content {

    /**
     * 메시지 유형을 반환한다.
     *
     * @return 메시지 유형
     */
    String getType();

}
