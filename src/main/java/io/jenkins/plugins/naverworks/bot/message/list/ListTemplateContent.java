package io.jenkins.plugins.naverworks.bot.message.list;

import io.jenkins.plugins.naverworks.UserConfiguration;
import io.jenkins.plugins.naverworks.bot.message.Action;
import io.jenkins.plugins.naverworks.bot.message.Content;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * List Template - Request content
 *
 * @see <a href="https://developers.worksmobile.com/kr/reference/bot-send-list?lang=ko#request-content">List Template - Request content</a>
 */
public class ListTemplateContent implements Content {

    /**
     * 메시지 유형
     */
    public static final String TYPE = "list_template";

    /**
     * 커버 데이터에 들어갈 데이터
     */
    private CoverData coverData;

    /**
     * 리스트 템플릿에 추가될 항목
     */
    private List<Element> elements;

    /**
     * 아래에 위치할 버튼.
     * 첫 번째 배열은 행, 두 번째 배열은 열을 나타낸다.
     */
    private List<List<Action>> actions;

    @Override
    public String getType() {
        return TYPE;
    }

    public CoverData getCoverData() {
        return coverData;
    }

    public List<Element> getElements() {
        return elements;
    }

    public List<List<Action>> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        List<List<Action>> contentActions = new ArrayList<>();
        contentActions.add(actions);

        this.actions = contentActions;
    }

    @Override
    public void writeMessage(UserConfiguration configuration) {
        final String backgroundImageUrl = configuration.getBackgroundImageUrl();
        String contentActionLabel = configuration.getContentActionLabel();
        final String contentActionLink = configuration.getContentActionLink();
        final List<Map<String, String>> messages = configuration.getMessages();

        this.coverData = new CoverData(backgroundImageUrl);

        if (isBlank(contentActionLabel)) {
            contentActionLabel = "more";
        }
        Action action = new Action(contentActionLabel, contentActionLink);
        this.setActions(Collections.singletonList(action));

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

            Action itemAction = new Action("more", link);
            Element element = new Element(title, subtitle, itemAction);

            elementList.add(element);
            elementCount++;
        }
        this.elements = elementList;
    }
}
