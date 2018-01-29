package pl.droidsonroids.jspoon;

import org.junit.Before;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class DefaultValueTest {
    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class Model {
        @Selector(value = "span.text", defValue = "NO_VALUE") String text;
        @Selector(value = "span.number", defValue = "-100") int number;
        @Selector(value = "span.bool", defValue = "true") boolean bool;
        @Selector(value = "ul.list", defValue = "NO_VALUE") List<String> list;

        @Selector(value = "span.another-text") String anotherText = "I have value";
        @Selector(value = "span.another-number") int anotherNumber = /* over */ 9000;
        @Selector(value = "span.bool") boolean anotherBool = true;
        @Selector(value = "ul.another-list") List<String> anotherList;
        @Selector(value = "div.another-submodel") SubModel anotherSubmodel = SubModel.FAKE;

        public Model() {
            anotherList = new ArrayList<>();
            anotherList.add("hiding");
        }
    }

    private static class SubModel {

        static final SubModel FAKE = new SubModel();

        @Selector(value = "p#name") String name = "fake";
    }

    @Test
    public void defaultValueTest() {
        HtmlAdapter<Model> htmlAdapter = jspoon.adapter(Model.class);
        Model model = htmlAdapter.fromHtml("<div></div>");
        assertEquals("NO_VALUE", model.text);
        assertEquals(-100, model.number);
        assertEquals(true, model.bool);
        assertEquals(new ArrayList<String>(), model.list);

        assertEquals("I have value", model.anotherText);
        assertEquals(9000, model.anotherNumber);
        assertEquals(true, model.anotherBool);
        List<String> expected = new ArrayList<>();
        expected.add("hiding");
        assertEquals(expected, model.anotherList);
        assertEquals(SubModel.FAKE, model.anotherSubmodel);
    }
}
