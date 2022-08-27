package io.jenkins.plugins.naverworks.bot;

import io.jenkins.plugins.naverworks.bot.message.CarouselContent;
import io.jenkins.plugins.naverworks.bot.message.LinkContent;
import io.jenkins.plugins.naverworks.bot.message.ListTemplateContent;
import io.jenkins.plugins.naverworks.bot.message.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NaverWorksMessageServiceTest {

    final int maxListTemplateContent = 4;
    static NaverWorksMessageService service;

    @BeforeAll
    static void setUp() {
        service = new NaverWorksMessageService();
    }

    @Test
    void if_message_is_empty_sut_returns_link_message() {
        List<Map<String, String>> messages = new ArrayList<>();

        Message message = service.write(
                messages,
                null,
                null,
                null
        );

        assertThat(message.getContent())
                .as("should be LinkContent")
                .isInstanceOf(LinkContent.class);
    }


    @ParameterizedTest
    @ValueSource(ints = maxListTemplateContent)
    void if_messages_size_is_list_template_size_sut_returns_list_template_message(int size) {
        List<Map<String, String>> messages = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            messages.add(contentFixture(i));
        }

        Message message = service.write(
                messages,
                null,
                null,
                null
        );

        assertThat(message.getContent())
                .as("should be ListTemplateContent")
                .isInstanceOf(ListTemplateContent.class);
    }

    @ParameterizedTest
    @ValueSource(ints = maxListTemplateContent + 1)
    void if_messages_size_is_more_than_list_template_size_sut_returns_carousel_message(int size) {
        List<Map<String, String>> messages = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            messages.add(contentFixture(i));
        }

        Message message = service.write(
                messages,
                null,
                null,
                null
        );

        assertThat(message.getContent())
                .as("should be CarouselContent")
                .isInstanceOf(CarouselContent.class);
    }

    Map<String, String> contentFixture(int index) {
        Map<String, String> content = new HashMap<>();
        content.put("link", "https://markruler.github.io/");
        content.put("title", String.format("MARK-%d", index));
        content.put("subtitle", String.format("Jira Issue %d", index));
        return content;
    }
}
