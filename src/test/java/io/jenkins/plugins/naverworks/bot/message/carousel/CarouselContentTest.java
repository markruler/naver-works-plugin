package io.jenkins.plugins.naverworks.bot.message.carousel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.naverworks.UserConfiguration;
import io.jenkins.plugins.naverworks.bot.message.MessageFixture;
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
        UserConfiguration configuration = new UserConfiguration(messages, null, null, null, null, null);

        CarouselContent carouselContent = new CarouselContent();
        carouselContent.writeMessage(configuration);

        // when
        List<Column> columns = carouselContent.getColumns();

        // then
        String actual = objectMapper.writeValueAsString(columns);
        String expected = "[{\"originalContentUrl\":null,\"field\":null,\"title\":\"MARK-1\",\"text\":\"Jira Issue 1\",\"defaultAction\":{\"label\":null,\"uri\":null,\"type\":\"uri\"},\"actions\":[{\"label\":\"more\",\"uri\":\"https://markruler.github.io/\",\"type\":\"uri\"}]}]";
        assertThat(actual).isEqualTo(expected);
    }

}
