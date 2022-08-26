package io.jenkins.plugins.naverworks.bot.message;

import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListTemplateContent implements Content {
    private static final String type = "list_template";
    private CoverData coverData;
    private List<Element> elements;
    private List<List<Action>> actions;

    @Override
    public String getType() {
        return type;
    }

    public CoverData getCoverData() {
        return coverData;
    }

    public void setCoverData(CoverData coverData) {
        this.coverData = coverData;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public void setMessages(List<Map<String, String>> messages) {
        List<Element> elementList = new ArrayList<>();
        // EXCEEDED_LENGTH_LIMIT_OF_PARAM: Maximum content.elements length is 4
        final int maxContentElementsLength = 4;
        int elementCount = 0;
        for (Map<String, String> message : messages) {
            if (elementCount == maxContentElementsLength) {
                break;
            }
            String link = MapUtils.getString(message, "link");
            String title = MapUtils.getString(message, "title");
            String subtitle = MapUtils.getString(message, "subtitle");

            Action itemAction = new Action("uri", "more", link);
            Element element = new Element(title, subtitle, itemAction);

            elementList.add(element);
            elementCount++;
        }

        this.setElements(elementList);
    }

    public List<List<Action>> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        List<List<Action>> contentActions = new ArrayList<>();
        contentActions.add(actions);

        this.actions = contentActions;
    }
}
