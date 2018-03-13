package pl.droidsonroids.jspoon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pl.droidsonroids.jspoon.annotation.Selector;

public class FieldInheritenceTest {

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

    private static class ParentModel {
        @Selector(".boolean") List<Boolean> booleanList;
        @Selector("p.int") BigDecimal baseNumber;
        @Selector("p.string") final static String PARENT_CONSTANT = "PARENT";
    }

    private static class ChildModel extends ParentModel {
        @Selector("p.boolean") List<Boolean> booleanList;
        @Selector("span.int") BigDecimal childNumber;
        @Selector("p.string") final static String CHILD_CONSTANT = "CHILD";
    }

    @SuppressWarnings("static-access")
    @Test
    public void testInheritedFields() {
        HtmlAdapter<ChildModel> htmlAdapter = jspoon.adapter(ChildModel.class);
        ChildModel model = htmlAdapter.fromHtml(HTML_CONTENT);

        assertEquals("PARENT", model.PARENT_CONSTANT);
        assertEquals("CHILD", model.CHILD_CONSTANT);
        assertEquals(new BigDecimal("-200"), model.childNumber);
        assertEquals(new BigDecimal("32000"), model.baseNumber);

        // @Selector overriding feature via 'field hiding' (using same name)
        assertEquals(1, model.booleanList.size());
        assertNull(((ParentModel)model).booleanList);
    }

    @SuppressWarnings("static-access")
    @Test
    public void testDowncastingInstanceFields() {
        HtmlAdapter<ParentModel> htmlAdapter = jspoon.adapter(ParentModel.class);
        ChildModel subclass = new ChildModel();
        ChildModel model = (ChildModel) htmlAdapter.fromHtml(HTML_CONTENT, subclass);

        assertEquals("PARENT", model.PARENT_CONSTANT);
        assertEquals("CHILD", model.CHILD_CONSTANT);

        // fields populated depend on adapter's T, so only inherited fields from
        // ParentModel are populated
        assertNull(model.childNumber);
        assertEquals(new BigDecimal("32000"), model.baseNumber);

        // a type of 'inverse override' when un'hidden
        assertNull(model.booleanList);
        assertEquals(3, ((ParentModel)model).booleanList.size());
    }
}