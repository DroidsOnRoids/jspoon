package pl.droidsonroids.jspoon;

import org.junit.Before;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;

import static org.junit.Assert.assertEquals;

public class DefaultValueTest {
    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class Model {
        @Selector(value = "span", defValue = "NO_VALUE") String text;
        @Selector(value = "p", defValue = "-100") int number;
    }

    @Test
    public void defaultValueTest() {
        HtmlAdapter<Model> htmlAdapter = jspoon.adapter(Model.class);
        Model model = htmlAdapter.fromHtml("<div></div>");
        assertEquals(model.text, "NO_VALUE");
        assertEquals(model.number, -100);
    }
}
