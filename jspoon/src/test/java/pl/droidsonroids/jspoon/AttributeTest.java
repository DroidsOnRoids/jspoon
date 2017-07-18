package pl.droidsonroids.jspoon;

import org.junit.Before;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;

import static org.junit.Assert.assertEquals;

public class AttributeTest {
    private final static String HTML_CONTENT = "<img "
            + "id='id' "
            + "src='/img.jpg' "
            + "alt='alt-text' "
            + "class='A B C' />"
            + "<div><p>test</p></div>";

    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class ImgAttributesModel {
        @Selector(value = "img", attr = "id") String id;
        @Selector(value = "img", attr = "src") String src;
        @Selector(value = "img", attr = "alt") String alt;
        @Selector(value = "img", attr = "class") String classes;
    }

    private static class HtmlAttributesModel {
        @Selector("div") String text;
        @Selector(value = "div", attr = "html") String html;
        @Selector(value = "div", attr = "innerHtml") String innerHtml;
        @Selector(value = "div", attr = "outerHtml") String outerHtml;

    }

    @Test
    public void simpleAttributes() {
        ImgAttributesModel imgAttributesModel = createObjectFromHtml(ImgAttributesModel.class);
        assertEquals(imgAttributesModel.id, "id");
        assertEquals(imgAttributesModel.src, "/img.jpg");
        assertEquals(imgAttributesModel.alt, "alt-text");
        assertEquals(imgAttributesModel.classes, "A B C");
    }

    @Test
    public void htmlAttributes() {
        HtmlAttributesModel htmlAttributesModel = createObjectFromHtml(HtmlAttributesModel.class);
        assertEquals(htmlAttributesModel.text, "test");
        assertEquals(htmlAttributesModel.html, "<p>test</p>");
        assertEquals(htmlAttributesModel.innerHtml, "<p>test</p>");
        assertEquals(htmlAttributesModel.outerHtml.replaceAll("[\n ]", ""), "<div><p>test</p></div>");
    }

    private <T> T createObjectFromHtml(Class<T> className) {
        HtmlAdapter<T> htmlAdapter = jspoon.adapter(className);
        return htmlAdapter.fromHtml(HTML_CONTENT);
    }
}
