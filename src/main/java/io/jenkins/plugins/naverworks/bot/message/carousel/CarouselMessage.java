package io.jenkins.plugins.naverworks.bot.message.carousel;

import io.jenkins.plugins.naverworks.bot.message.Message;

/**
 * Message - Carousel
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-carousel">Carousel</a>
 */
public class CarouselMessage implements Message {

    /**
     * 메시지 내용
     */
    private final CarouselContent content;

    public CarouselMessage(CarouselContent content) {
        this.content = content;
    }

    @Override
    public CarouselContent getContent() {
        return content;
    }
}
