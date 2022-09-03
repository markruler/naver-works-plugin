package io.jenkins.plugins.naverworks.bot.message.list;

/**
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-list?lang=ko#request-content">List Template - Request Content</a>
 */
public class CoverData {

    /**
     * 커버 데이터에 들어갈 데이터
     */
    private String backgroundImageUrl;

    public CoverData() {
    }

    public CoverData(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }
}
