package io.jenkins.plugins.naverworks.bot.message;

public class TextContent implements Content {

    /**
     * 메시지 유형
     */
    private static final String type = "text";

    /**
     * 메시지 본문
     */
    private final String text;

    public TextContent(String text) {
        this.text = text;
    }

    @Override
    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}