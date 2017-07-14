package pl.droidsonroids.jspoon;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;

import static org.junit.Assert.assertEquals;

public class SimpleTypesList {
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
    }

    private static class IntModel {
        @Selector(".int") List<Integer> integerList;
    }
    private static class StringModel {
        @Selector(".string") List<String> stringList;
    }

    @Test
    public void booleanList() {
        HtmlAdapter<BooleanModel> htmlAdapter = jspoon.adapter(BooleanModel.class);
        BooleanModel booleanModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(booleanModel.booleanList, Arrays.asList(true, false, false));
    }

    @Test
    public void integerList() {
        HtmlAdapter<IntModel> htmlAdapter = jspoon.adapter(IntModel.class);
        IntModel intModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(intModel.integerList, Arrays.asList(-200, 4, 32000));
    }

    @Test
    public void stringList() {
        HtmlAdapter<StringModel> htmlAdapter = jspoon.adapter(StringModel.class);
        StringModel stringModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(stringModel.stringList, Arrays.asList("Test1", "", "Test2 ".trim()));
    }
}