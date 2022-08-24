package io.jenkins.plugins.naverworks.bot;

public class Bot {
    private final String id;
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
