package io.jenkins.plugins.naverworks;

import java.util.List;
import java.util.Map;

/**
 * 사용자 설정 파라미터
 */
public class UserConfiguration {

    private final List<Map<String, String>> messages;
    private final String backgroundImageUrl;
    private final String contentActionLabel;
    private final String contentActionLink;
    private final String simpleMessage;
    private final String messageType;

    public UserConfiguration(
            List<Map<String, String>> messages,
            String backgroundImageUrl,
            String contentActionLabel,
            String contentActionLink,
            String simpleMessage,
            String messageType
    ) {
        this.messages = messages;
        this.backgroundImageUrl = backgroundImageUrl;
        this.contentActionLabel = contentActionLabel;
        this.contentActionLink = contentActionLink;
        if (simpleMessage == null || simpleMessage.isEmpty()) {
            simpleMessage = "Changes have been deployed.";
        }
        this.simpleMessage = simpleMessage;
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

    public String getSimpleMessage() {
        return simpleMessage;
    }

    public String getMessageType() {
        return messageType;
    }
}
