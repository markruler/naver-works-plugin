package io.jenkins.plugins.naverworks.bot;

public class Element {
    private String title;
    private String subtitle;
    private Action action;

    public Element() {
    }

    public Element(String title, String subtitle, Action action) {
        this.title = title;
        this.subtitle = subtitle;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
