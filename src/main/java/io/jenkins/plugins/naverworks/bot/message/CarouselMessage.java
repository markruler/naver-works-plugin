package io.jenkins.plugins.naverworks.bot.message;

/**
 * Message - Carousel
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-carousel">Carousel</a>
 */
public class CarouselMessage implements Message {

    private final Content content;

    public CarouselMessage(Content content) {
        this.content = content;
    }

    @Override
    public Content getContent() {
        return content;
    }
}
