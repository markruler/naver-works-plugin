package io.jenkins.plugins.naverworks.bot.message.link;

import io.jenkins.plugins.naverworks.UserConfiguration;
import io.jenkins.plugins.naverworks.bot.message.Content;

/**
 * Link - Request content
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-link?lang=ko#request-content">Link</a>
 */
public class LinkContent implements Content {

    /**
     * 메시지 유형
     */
    public static final String TYPE = "link";

    /**
     * 메시지 본문
     */
    private String contentText;

    /**
     * 링크 레이블
     */
    private String linkText;

    /**
     * linkText 영역을 누르면 이동할 페이지
     */
    private String link;

    @Override
    public String getType() {
        return TYPE;
    }

    public String getContentText() {
        return contentText;
    }

    public String getLinkText() {
        return linkText;
    }

    public String getLink() {
        return link;
    }

    @Override
    public void writeMessage(UserConfiguration configuration) {
        this.contentText = configuration.getSimpleMessage();
        this.linkText = configuration.getContentActionLabel();
        this.link = configuration.getContentActionLink();
    }
}
