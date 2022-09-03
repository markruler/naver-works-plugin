package io.jenkins.plugins.naverworks.bot;

public class Bot {

    /**
     * 메시지 Bot ID
     */
    private final String id;

    /**
     * 메시지방 ID
     */
    private final String channelId;

    public Bot(String id, String channelId) {
        this.id = id;
        this.channelId = channelId;
    }

    public String getId() {
        return id;
    }

    public String getChannelId() {
        return channelId;
    }
}
