package io.jenkins.plugins.naverworks.bot.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFixture {

    private MessageFixture() {
    }

    public static List<Map<String, String>> generate(int size) {
        List<Map<String, String>> messages = new ArrayList<>();
        if (size < 1) {
            return messages;
        }

        for (int index = 1; index <= size; index++) {
            Map<String, String> content = new HashMap<>();
            content.put("link", "https://markruler.github.io/");
            content.put("title", String.format("MARK-%d", index));
            content.put("subtitle", String.format("Jira Issue %d", index));
            messages.add(content);
        }
        return messages;
    }

}
