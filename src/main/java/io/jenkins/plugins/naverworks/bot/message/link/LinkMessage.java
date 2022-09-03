package io.jenkins.plugins.naverworks.bot.message.link;

import io.jenkins.plugins.naverworks.bot.message.Content;
import io.jenkins.plugins.naverworks.bot.message.Message;

/**
 * Message - Link
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-link">Link</a>
 */
public class LinkMessage implements Message {

    /**
     * 메시지 내용
     */
    private final Content content;

    public LinkMessage(Content content) {
        this.content = content;
    }

    @Override
    public Content getContent() {
        return content;
    }
}
