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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NaverWorksMessageServiceTest {

    static NaverWorksMessageService service;

    @BeforeAll
    static void setUp() {
        service = new NaverWorksMessageService();
    }

    @Nested
    @DisplayName("TextMessage는")
    class Describe_TextMessage {

        @Nested
        @DisplayName("notification이 비어 있다면")
        class Context_notification_is_empty {

            @ParameterizedTest
            @ValueSource(strings = TextContent.TYPE)
            @DisplayName("기본 메시지를 출력한다")
            void message_type_text(String messageType) {

                int size = 0;
                List<Map<String, String>> messages = MessageFixture.generate(size);
                String backgroundImageUrl = null;
                String contentActionLabel = null;
                String contentActionLink = null;
                String notification = null;

                UserConfiguration userConfiguration =
                        new UserConfiguration(
                                messages,
                                backgroundImageUrl,
                                contentActionLabel,
                                contentActionLink,
                                notification,
                                messageType
                        );
                Message message = service.write(userConfiguration);

                Content content = message.getContent();
                assertThat(content).isInstanceOf(TextContent.class);
                assertThat(((TextContent) content).getText()).isEqualTo("Changes have been deployed.");
            }
        }

        @Nested
        @DisplayName("notification이 있다면")
        class Context_notification_is_not_empty {

            @ParameterizedTest
            @ValueSource(strings = TextContent.TYPE)
            @DisplayName("notifiaction을 출력한다")
            void if_text_message_has_notification_sut_returns_notification(String messageType) {

                int size = 0;
                List<Map<String, String>> messages = MessageFixture.generate(size);
                String backgroundImageUrl = null;
                String contentActionLabel = null;
                String contentActionLink = null;
                String notification = "Notify";

                UserConfiguration userConfiguration =
                        new UserConfiguration(
                                messages,
                                backgroundImageUrl,
                                contentActionLabel,
                                contentActionLink,
                                notification,
                                messageType
                        );
                Message message = service.write(userConfiguration);

                Content content = message.getContent();
                assertThat(content).isInstanceOf(TextContent.class);
                assertThat(((TextContent) content).getText()).isEqualTo(notification);
            }
        }

    }


    @Nested
    @DisplayName("LinkMessage는")
    class Describe_LinkMessage {

        @Nested
        @DisplayName("notification이 있다면")
        class Context_notification_is_not_empty {

            @ParameterizedTest
            @ValueSource(strings = LinkContent.TYPE)
            @DisplayName("notification을 출력한다")
            void message_type_link(String messageType) {

                int size = 0;
                List<Map<String, String>> messages = MessageFixture.generate(size);
                String backgroundImageUrl = null;
                String contentActionLabel = "Go to Jenkins";
                String contentActionLink = "https://www.jenkins.io/";
                String notification = "Notify";

                UserConfiguration userConfiguration =
                        new UserConfiguration(
                                messages,
                                backgroundImageUrl,
                                contentActionLabel,
                                contentActionLink,
                                notification,
                                messageType
                        );
                Message message = service.write(userConfiguration);

                Content content = message.getContent();
                assertThat(content).isInstanceOf(LinkContent.class);
                assertThat(((LinkContent) content).getContentText()).isEqualTo(notification);
            }
        }

    }

    @Nested
    @DisplayName("ListTemplateMessage는")
    class Describe_ListTemplate {

        @ParameterizedTest
        @ValueSource(strings = ListTemplateContent.TYPE)
        @DisplayName("messageType이 list_template일 경우 생성된다")
        void message_type_list_template(String messageType) {

            int size = 4;
            List<Map<String, String>> messages = MessageFixture.generate(size);
            String backgroundImageUrl = null;
            String contentActionLabel = null;
            String contentActionLink = null;
            String notification = null;

            UserConfiguration userConfiguration =
                    new UserConfiguration(
                            messages,
                            backgroundImageUrl,
                            contentActionLabel,
                            contentActionLink,
                            notification,
                            messageType
                    );
            Message message = service.write(userConfiguration);

            assertThat(message.getContent()).isInstanceOf(ListTemplateContent.class);
        }
    }

    @Nested
    @DisplayName("CarouselMessage는")
    class Describe_CarouselMessage {

        @ParameterizedTest
        @ValueSource(strings = CarouselContent.TYPE)
        @DisplayName("messageType이 carousel일 경우 생성된다")
        void message_type_carousel(String messageType) {

            int size = 5;
            List<Map<String, String>> messages = MessageFixture.generate(size);
            String backgroundImageUrl = null;
            String contentActionLabel = null;
            String contentActionLink = null;
            String notification = null;

            UserConfiguration userConfiguration =
                    new UserConfiguration(
                            messages,
                            backgroundImageUrl,
                            contentActionLabel,
                            contentActionLink,
                            notification,
                            messageType
                    );
            Message message = service.write(userConfiguration);

            assertThat(message.getContent()).isInstanceOf(CarouselContent.class);
        }
    }
}
