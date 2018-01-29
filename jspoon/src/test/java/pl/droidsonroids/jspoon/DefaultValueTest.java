package pl.droidsonroids.jspoon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import pl.droidsonroids.jspoon.annotation.Selector;

public class DefaultValueTest {
    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class Model {
        @Selector(value = "span", defValue = "DEFAULT_VALUE") String text;
        @Selector(value = "p", defValue = "-100") int number;

        @Selector(value = "span", defValue = "hello") String text2 = "world";
        @Selector(value = "span") String text3 = "helloworld";
        @Selector(value = "span") String text4;
    }

    @Test
    public void defaultValueTest() {
        HtmlAdapter<Model> htmlAdapter = jspoon.adapter(Model.class);
        Model model = htmlAdapter.fromHtml("<div></div>");
        assertEquals("DEFAULT_VALUE", model.text); // since defValue explicitly defined
        assertEquals("hello", model.text2); // defValue takes precedent as its whatever would be parsed from Element
        assertEquals("helloworld", model.text3); // no defValue, let's leave whatever is set
        assertNull(model.text4); // should not be set to anything silently if developer did not set a defValue
        assertEquals(-100, model.number);
    }
}
