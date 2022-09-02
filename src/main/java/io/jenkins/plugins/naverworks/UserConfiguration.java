package io.jenkins.plugins.naverworks;

import java.util.List;
import java.util.Map;

public class UserConfiguration {

    private final List<Map<String, String>> messages;
    private final String backgroundImageUrl;
    private final String contentActionLabel;
    private final String contentActionLink;
    private final String notification;
    private final String messageType;

    public UserConfiguration(
            List<Map<String, String>> messages,
            String backgroundImageUrl,
            String contentActionLabel,
            String contentActionLink,
            String notification,
            String messageType
    ) {
        this.messages = messages;
        this.backgroundImageUrl = backgroundImageUrl;
        this.contentActionLabel = contentActionLabel;
        this.contentActionLink = contentActionLink;
        this.notification = notification;
        this.messageType = messageType;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public String getContentActionLabel() {
        return contentActionLabel;
    }

    public String getContentActionLink() {
        return contentActionLink;
    }

    public String getNotification() {
        return notification;
    }

    public String getMessageType() {
        return messageType;
    }
}
