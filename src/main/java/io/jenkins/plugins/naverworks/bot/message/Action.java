package io.jenkins.plugins.naverworks.bot.message;

/**
 * Action Object - URI Action
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-actionobject?lang=ko#uri-action">Actions Objects</a>
 */
public class Action {

    /**
     * uri로 고정
     */
    private final String type = "uri";

    /**
     * 항목에 표시되는 레이블
     */
    private final String label;

    /**
     * 항목을 누를 때 접속할 URI
     */
    private final String uri;

    public Action(String label, String uri) {
        this.label = label;
        this.uri = uri;
    }

    @Deprecated
    public Action(@SuppressWarnings("unused") String ignore, String label, String uri) {
        this.label = label;
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getUri() {
        return uri;
    }

}
