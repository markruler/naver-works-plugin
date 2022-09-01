package io.jenkins.plugins.naverworks.bot;

import io.jenkins.plugins.naverworks.UserConfiguration;
import io.jenkins.plugins.naverworks.bot.message.CarouselContent;
import io.jenkins.plugins.naverworks.bot.message.Content;
import io.jenkins.plugins.naverworks.bot.message.LinkContent;
import io.jenkins.plugins.naverworks.bot.message.ListTemplateContent;
import io.jenkins.plugins.naverworks.bot.message.Message;
import io.jenkins.plugins.naverworks.bot.message.MessageFixture;
import io.jenkins.plugins.naverworks.bot.message.TextContent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(ints = 0)
    void if_message_is_empty_sut_returns_text_message(int size) {

        List<Map<String, String>> messages = MessageFixture.generate(size);
        String backgroundImageUrl = null;
        String contentActionLabel = null;
        String contentActionLink = null;
        String notification = null;
        String contentType = null;

        UserConfiguration userConfiguration =
                new UserConfiguration(
                        messages,
                        backgroundImageUrl,
                        contentActionLabel,
                        contentActionLink,
                        notification,
                        contentType
                );
        Message message = service.write(userConfiguration);

        Content content = message.getContent();
        assertThat(content).isInstanceOf(TextContent.class);
        assertThat(((TextContent) content).getText()).isEqualTo("Changes have been deployed.");
    }

    @ParameterizedTest
    @ValueSource(ints = 0)
    void if_message_is_empty_but_notification_exists_sut_returns_text_message(int size) {

        List<Map<String, String>> messages = MessageFixture.generate(size);
        String backgroundImageUrl = null;
        String contentActionLabel = null;
        String contentActionLink = null;
        String notification = "Notify";
        String contentType = null;

        UserConfiguration userConfiguration =
                new UserConfiguration(
                        messages,
                        backgroundImageUrl,
                        contentActionLabel,
                        contentActionLink,
                        notification,
                        contentType
                );
        Message message = service.write(userConfiguration);

        Content content = message.getContent();
        assertThat(content).isInstanceOf(TextContent.class);
        assertThat(((TextContent) content).getText()).isEqualTo(notification);
    }

    @ParameterizedTest
    @ValueSource(ints = 0)
    void if_message_is_empty_but_content_link_exists_sut_returns_link_message(int size) {

        List<Map<String, String>> messages = MessageFixture.generate(size);
        String backgroundImageUrl = null;
        String contentActionLabel = "Go to Jenkins";
        String contentActionLink = "https://www.jenkins.io/";
        String notification = "Notify";
        String contentType = null;

        UserConfiguration userConfiguration =
                new UserConfiguration(
                        messages,
                        backgroundImageUrl,
                        contentActionLabel,
                        contentActionLink,
                        notification,
                        contentType
                );
        Message message = service.write(userConfiguration);

        Content content = message.getContent();
        assertThat(content).isInstanceOf(LinkContent.class);
        assertThat(((LinkContent) content).getContentText()).isEqualTo(notification);
    }

    @ParameterizedTest
    @ValueSource(ints = maxListTemplateContent)
    void if_messages_size_is_list_template_size_sut_returns_list_template_message(int size) {

        List<Map<String, String>> messages = MessageFixture.generate(size);
        String backgroundImageUrl = null;
        String contentActionLabel = null;
        String contentActionLink = null;
        String notification = null;
        String contentType = null;

        UserConfiguration userConfiguration =
                new UserConfiguration(
                        messages,
                        backgroundImageUrl,
                        contentActionLabel,
                        contentActionLink,
                        notification,
                        contentType
                );
        Message message = service.write(userConfiguration);

        assertThat(message.getContent()).isInstanceOf(ListTemplateContent.class);
    }

    @ParameterizedTest
    @ValueSource(ints = maxListTemplateContent + 1)
    void if_messages_size_is_more_than_list_template_size_sut_returns_carousel_message(int size) {

        List<Map<String, String>> messages = MessageFixture.generate(size);
        String backgroundImageUrl = null;
        String contentActionLabel = null;
        String contentActionLink = null;
        String notification = null;
        String contentType = null;

        UserConfiguration userConfiguration =
                new UserConfiguration(
                        messages,
                        backgroundImageUrl,
                        contentActionLabel,
                        contentActionLink,
                        notification,
                        contentType
                );
        Message message = service.write(userConfiguration);

        assertThat(message.getContent()).isInstanceOf(CarouselContent.class);
    }
}
