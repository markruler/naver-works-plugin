package io.jenkins.plugins.naverworks.bot;

import java.util.List;

public class ListTemplateContent implements Content {
    private String type = "list_template";
    private CoverData coverData;
    private List<Element> elements;
    private List<List<Action>> actions;

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

    public List<List<Action>> getActions() {
        return actions;
    }

    public void setActions(List<List<Action>> actions) {
        this.actions = actions;
    }
}
