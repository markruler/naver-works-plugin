package io.jenkins.plugins.naverworks.bot.message;

import io.jenkins.plugins.naverworks.UserConfiguration;

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

    /**
     * 메시지를 작성한다.
     *
     * @param configuration 사용자 설정
     */
    void writeMessage(UserConfiguration configuration);

}
