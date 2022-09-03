package io.jenkins.plugins.naverworks.bot.message.list;

import io.jenkins.plugins.naverworks.bot.message.Action;

/**
 * List Template에 추가될 항목
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-list?lang=ko#request-content">List Template - Request Content</a>
 */
public class Element {

    /**
     * 각 항목의 제목
     */
    private final String title;

    /**
     * 각 항목의 부제목
     */
    private final String subtitle;

    /**
     * 각 항목에 사용될 버튼
     */
    private final Action action;

    public Element(String title, String subtitle, Action action) {
        this.title = title;
        this.subtitle = subtitle;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Action getAction() {
        return action;
    }

}
