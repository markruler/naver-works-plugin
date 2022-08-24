package io.jenkins.plugins.naverworks.bot;

/**
 * Action Object
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-actionobject">Actions Objects</a>
 */
public class Action {
    private String type;
    private String label;
    private String uri;

    public Action() {
    }

    public Action(String type, String label, String uri) {
        this.type = type;
        this.label = label;
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
