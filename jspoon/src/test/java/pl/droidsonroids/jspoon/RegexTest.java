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
        @Selector(value = "div", format = "([a-z]+),") String number;
    }

    @Test
    public void regexTest() {
        HtmlAdapter<RegexModel> htmlAdapter = jspoon.adapter(RegexModel.class);
        RegexModel regexModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(regexModel.number, "three");
    }
}
