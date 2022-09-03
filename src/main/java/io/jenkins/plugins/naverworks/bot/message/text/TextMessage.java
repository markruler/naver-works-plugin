package io.jenkins.plugins.naverworks.bot.message.text;

import io.jenkins.plugins.naverworks.bot.message.Content;
import io.jenkins.plugins.naverworks.bot.message.Message;

/**
 * Message - Text
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-text">Text</a>
 */
public class TextMessage implements Message {

    /**
     * 메시지 내용
     */
    private final Content content;

    public TextMessage(Content content) {
        this.content = content;
    }

    @Override
    public Content getContent() {
        return content;
    }
}
