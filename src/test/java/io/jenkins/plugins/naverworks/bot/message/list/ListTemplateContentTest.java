package io.jenkins.plugins.naverworks.bot.message.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.naverworks.bot.message.MessageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ListTemplateContentTest {

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void list_template_content_response() throws JsonProcessingException {
        // given
        List<Map<String, String>> messages = MessageFixture.generate(1);

        ListTemplateContent listTemplateContent = new ListTemplateContent();
        listTemplateContent.setMessages(messages, null, null, null);

        // when
        List<Element> elements = listTemplateContent.getElements();

        // then
        String actual = objectMapper.writeValueAsString(elements);
        String expected = "[{\"title\":\"MARK-1\",\"subtitle\":\"Jira Issue 1\",\"action\":{\"label\":\"more\",\"uri\":\"https://markruler.github.io/\",\"type\":\"uri\"}}]";
        assertThat(actual).isEqualTo(expected);
    }
}
