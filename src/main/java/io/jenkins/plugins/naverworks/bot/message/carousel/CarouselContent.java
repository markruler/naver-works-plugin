package io.jenkins.plugins.naverworks.bot.message.carousel;

import io.jenkins.plugins.naverworks.UserConfiguration;
import io.jenkins.plugins.naverworks.bot.message.Action;
import io.jenkins.plugins.naverworks.bot.message.Content;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Carousel - Request content
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-carousel">Carousel</a>
 */
public class CarouselContent implements Content {

    /**
     * 메시지 유형
     */
    public static final String TYPE = "carousel";

    /**
     * 이미지의 비율.
     * 모든 열에 적용된다.
     */
    private String imageAspectRatio;

    /**
     * 이미지의 사이즈.
     * 모든 열에 적용된다.
     */
    private String imageSize;

    /**
     * 캐러셀의 객체 목록
     */
    private List<Column> columns;

    @Override
    public String getType() {
        return TYPE;
    }

    public String getImageAspectRatio() {
        return imageAspectRatio;
    }

    public void setImageAspectRatio(String imageAspectRatio) {
        this.imageAspectRatio = imageAspectRatio;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public void writeMessage(UserConfiguration configuration) {
        final List<Map<String, String>> messages = configuration.getMessages();
        final String backgroundImageUrl = configuration.getBackgroundImageUrl();
        final String contentActionLink = configuration.getContentActionLink();

        List<Column> columnList = new ArrayList<>();
        // EXCEEDED_LENGTH_LIMIT_OF_PARAM: Maximum content.columns length is 10
        final int maxContentColumnsLength = 10;
        int columnCount = 0;
        for (Map<String, String> message : messages) {
            if (columnCount == maxContentColumnsLength) {
                break;
            }
            String link = MapUtils.getString(message, "link");
            String title = MapUtils.getString(message, "title");
            String subtitle = MapUtils.getString(message, "subtitle");

            Column column = new Column(
                    backgroundImageUrl,
                    null,
                    title,
                    subtitle,
                    new Action(null, contentActionLink),
                    Collections.singletonList(new Action("more", link))
            );

            columnList.add(column);
            columnCount++;
        }

        this.setColumns(columnList);
    }
}
