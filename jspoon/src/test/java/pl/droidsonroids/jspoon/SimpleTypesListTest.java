package pl.droidsonroids.jspoon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;

import static org.junit.Assert.assertEquals;

public class SimpleTypesListTest {
    private final static String HTML_CONTENT = "<div>"
            + "<span class='string'>Test1</span>"
            + "<span class='int'>-200</span>"
            + "<span class='boolean'>true</span>"
            + "<div class='string'></div>"
            + "<div class='int'>4</div>"
            + "<div class='boolean'>false</div>"
            + "<p class='string'>Test2 </p>"
            + "<p class='int'>32000</p>"
            + "<p class='boolean'>test</p>"
            + "</div>";
    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class BooleanModel {
        @Selector(".boolean") List<Boolean> booleanList;
        @Selector(".boolean") ArrayList<Boolean> booleanArrayList;
    }

    private static class IntModel {
        @Selector(".int") List<Integer> integerList;
        @Selector(".int") ArrayList<Integer> integerArrayList;
    }
    private static class StringModel {
        @Selector(".string") List<String> stringList;
        @Selector(".string") ArrayList<String> stringArrayList;
    }

    @Test
    public void booleanLists() {
        BooleanModel booleanModel = createObjectFromHtml(BooleanModel.class);
        assertEquals(booleanModel.booleanList, Arrays.asList(true, false, false));
        assertEquals(booleanModel.booleanArrayList, new ArrayList<>(Arrays.asList(true, false, false)));
    }

    @Test
    public void integerLists() {
        IntModel intModel = createObjectFromHtml(IntModel.class);
        assertEquals(intModel.integerList, Arrays.asList(-200, 4, 32000));
        assertEquals(intModel.integerArrayList, new ArrayList<>(Arrays.asList(-200, 4, 32000)));
    }

    @Test
    public void stringLists() {
        StringModel stringModel = createObjectFromHtml(StringModel.class);
        assertEquals(stringModel.stringList, Arrays.asList("Test1", "", "Test2 ".trim()));
        assertEquals(stringModel.stringArrayList, new ArrayList<>(Arrays.asList("Test1", "", "Test2 ".trim())));
    }

    private <T> T createObjectFromHtml(Class<T> className) {
        HtmlAdapter<T> htmlAdapter = jspoon.adapter(className);
        return htmlAdapter.fromHtml(HTML_CONTENT);
    }
}