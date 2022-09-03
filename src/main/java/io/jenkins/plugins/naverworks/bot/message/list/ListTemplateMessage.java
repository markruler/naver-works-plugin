package io.jenkins.plugins.naverworks.bot.message.list;

import io.jenkins.plugins.naverworks.bot.message.Message;

/**
 * Message - List Template
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-list">List Template</a>
 */
public class ListTemplateMessage implements Message {

    /**
     * 메시지 내용
     */
    private final ListTemplateContent content;

    public ListTemplateMessage(ListTemplateContent content) {
        this.content = content;
    }

    @Override
    public ListTemplateContent getContent() {
        return content;
    }
}
