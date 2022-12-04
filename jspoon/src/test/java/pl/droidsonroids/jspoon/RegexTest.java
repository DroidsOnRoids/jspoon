package pl.droidsonroids.jspoon;

import org.junit.Before;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;

import static org.junit.Assert.assertEquals;

public class RegexTest {
    private final static String HTML_CONTENT = "<div>"
            + "ONE, TwO, three,"
            + "</div>";

    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class RegexModel {
        @Selector(value = "div", regex = "([a-z]+),") String number;
    }

    private static class RegexModelDefault {
        @Selector(value = "div", regex = "(\\d+)", defValue = "1") int number;
    }


    @Test
    public void regexTest() {
        HtmlAdapter<RegexModel> htmlAdapter = jspoon.adapter(RegexModel.class);
        RegexModel regexModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(regexModel.number, "three");
    }


    @Test
    public void regexDefaultTest() {
        HtmlAdapter<RegexModelDefault> htmlAdapter = jspoon.adapter(RegexModelDefault.class);
        RegexModelDefault regexModelDefault = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(regexModelDefault.number, 1);
    }
}
