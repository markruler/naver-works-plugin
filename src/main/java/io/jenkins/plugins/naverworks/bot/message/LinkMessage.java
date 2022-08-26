package io.jenkins.plugins.naverworks.bot.message;

/**
 * Message - Link
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-link">Link</a>
 */
public class LinkMessage implements Message {

    private final Content content;

    public LinkMessage(Content content) {
        this.content = content;
    }

    @Override
    public Content getContent() {
        return content;
    }
}
