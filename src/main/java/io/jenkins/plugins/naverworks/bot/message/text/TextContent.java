package io.jenkins.plugins.naverworks.bot.message.text;

import io.jenkins.plugins.naverworks.UserConfiguration;
import io.jenkins.plugins.naverworks.bot.message.Content;

public class TextContent implements Content {

    /**
     * 메시지 유형
     */
    public static final String TYPE = "text";

    /**
     * 메시지 본문
     */
    private String text;

    @Override
    public String getType() {
        return TYPE;
    }

    public String getText() {
        return text;
    }

    @Override
    public void writeMessage(UserConfiguration configuration) {
        this.text = configuration.getSimpleMessage();
    }
}
