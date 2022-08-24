package io.jenkins.plugins.naverworks.bot;

public class CoverData {
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
