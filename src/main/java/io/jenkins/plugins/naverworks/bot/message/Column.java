package io.jenkins.plugins.naverworks.bot.message;

import java.util.List;

public class Column {
    /**
     * 이미지 URL(PNG 형식, HTTPS만 허용)<br>
     * originalContentUrl, fileId 중 하나만 지정해야 하며, 필수는 아니다.
     */
    private final String originalContentUrl;

    /**
     * 이미지 파일 ID
     */
    private final String field;

    /**
     * 제목
     */
    private final String title;

    /**
     * 메시지 내용
     */
    private final String text;

    /**
     * image, title, text 영역을 눌렀을 때 동작
     */
    private final Action defaultAction;

    /**
     * Action이 정의된 버튼 (최대 3개)
     */
    private final List<Action> actions;

    public Column(String originalContentUrl, String field, String title, String text, Action defaultAction, List<Action> actions) {
        this.originalContentUrl = originalContentUrl;
        this.field = field;
        this.title = title;
        this.text = text;
        this.defaultAction = defaultAction;
        this.actions = actions;
    }

    public String getOriginalContentUrl() {
        return originalContentUrl;
    }

    public String getField() {
        return field;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Action getDefaultAction() {
        return defaultAction;
    }

    public List<Action> getActions() {
        return actions;
    }
}
