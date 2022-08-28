package io.jenkins.plugins.naverworks.bot.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CarouselContentTest {

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void carousel_content_response() throws JsonProcessingException {
        // given
        List<Map<String, String>> messages = MessageFixture.generate(1);

        CarouselContent carouselContent = new CarouselContent();
        carouselContent.setMessages(messages, null, null);

        // when
        List<Column> columns = carouselContent.getColumns();

        // then
        String actual = objectMapper.writeValueAsString(columns);
        String expected = "[{\"originalContentUrl\":null,\"field\":null,\"title\":\"MARK-1\",\"text\":\"Jira Issue 1\",\"defaultAction\":{\"type\":\"uri\",\"label\":null,\"uri\":null},\"actions\":[{\"type\":\"uri\",\"label\":\"more\",\"uri\":\"https://markruler.github.io/\"}]}]";
        assertThat(actual).isEqualTo(expected);
    }

}
