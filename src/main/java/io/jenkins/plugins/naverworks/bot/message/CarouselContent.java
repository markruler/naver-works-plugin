package io.jenkins.plugins.naverworks.bot.message;

import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CarouselContent implements Content {
    private static final String type = "carousel";
    private String imageAspectRatio;
    private String imageSize;
    private List<Column> columns;

    @Override
    public String getType() {
        return type;
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

    public void setMessages(List<Map<String, String>> messages) {
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
                    null,
                    null,
                    title,
                    subtitle,
                    null,
                    Collections.singletonList(new Action("uri", "more", link))
            );

            columnList.add(column);
            columnCount++;
        }

        this.setColumns(columnList);
    }
}
