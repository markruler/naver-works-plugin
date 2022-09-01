package io.jenkins.plugins.naverworks.bot.message;

/**
 * Message - Text
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-text">Text</a>
 */
public class TextMessage implements Message {

    private final Content content;

    public TextMessage(Content content) {
        this.content = content;
    }

    @Override
    public Content getContent() {
        return content;
    }
}
