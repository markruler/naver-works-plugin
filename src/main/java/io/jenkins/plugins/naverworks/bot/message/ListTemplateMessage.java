package io.jenkins.plugins.naverworks.bot.message;

/**
 * Message - List Template
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-list">List Template</a>
 */
public class ListTemplateMessage implements Message {

    private final Content content;

    public ListTemplateMessage(Content content) {
        this.content = content;
    }

    @Override
    public Content getContent() {
        return content;
    }
}
