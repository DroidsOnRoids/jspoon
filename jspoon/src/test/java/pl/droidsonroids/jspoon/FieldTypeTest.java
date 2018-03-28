package pl.droidsonroids.jspoon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.rule.CustomLocaleRule;

public class FieldTypeTest {

    @Rule
    public CustomLocaleRule customLocaleRule = new CustomLocaleRule(Locale.US);

    private final static String HTML_CONTENT = "<div>"
            + "<span class='positive number'>123</span>"
            + "<span class='negative'>-200</span>"
            + "<span class='float'>3.14</span>"
            + "<div class='positive'>3366</div>"
            + "<div class='negative'>-4</div>"
            + "<div class='float'>9.98</div>"
            + "<p class='positive'>1314 </p>"
            + "<p class='negative'>-1</p>"
            + "<p class='float'>2.76</p>"
            + "</div>";
    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }
    static class GenericModel<T> {
        @Selector(".number")
        T item;
    }

    static class StringListModel extends GenericModel<List<String>> {
    }

    @Test
    public void testGenericStringList() throws Exception {
        HtmlAdapter<StringListModel> htmlAdapter = Jspoon.create().adapter(StringListModel.class);
        StringListModel model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertNotNull(model.item);
        assertFalse(model.item.isEmpty());
        assertEquals("123", model.item.get(0));
    }

    static class StringModel extends GenericModel<String>{
    }

    @Test
    public void testGenericString() throws Exception {
        HtmlAdapter<StringModel> htmlAdapter = Jspoon.create().adapter(StringModel.class);
        StringModel model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertNotNull(model.item);
        assertEquals("123", model.item);
    }

    static class StringArrayModel extends GenericModel<String[]>{
    }

    @Test
    public void testGenericStringArray() throws Exception {
        HtmlAdapter<StringArrayModel> htmlAdapter = Jspoon.create().adapter(StringArrayModel.class);
        StringArrayModel model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertNotNull(model.item);
        assertEquals("123", model.item[0]);
    }

    static class PrimitiveArrayModel extends GenericModel<int[]>{
    }

    @Test
    public void testGenericPrimitivesArray() throws Exception {
        HtmlAdapter<PrimitiveArrayModel> htmlAdapter = Jspoon.create().adapter(PrimitiveArrayModel.class);
        PrimitiveArrayModel model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertNotNull(model.item);
        assertEquals(123, model.item[0]);
    }

    static class LinkedListModel {
        @Selector(".number") LinkedList<String> list;
    }

    @Test
    public void testLinkedList() throws Exception {
        HtmlAdapter<LinkedListModel> htmlAdapter = Jspoon.create().adapter(LinkedListModel.class);
        LinkedListModel model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertNotNull(model.list);
        assertFalse(model.list.isEmpty());
        assertEquals("123", model.list.get(0));
    }

    static class RawModel {
        @SuppressWarnings("rawtypes")
        @Selector(".number") List list;
    }

    @Test
    public void testRawList() throws Exception {
        HtmlAdapter<RawModel> htmlAdapter = Jspoon.create().adapter(RawModel.class);
        RawModel model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertNotNull(model.list);
        assertFalse(model.list.isEmpty());
        assertEquals("123", model.list.get(0));
    }

    static class WildcardModel {
        @Selector(".number") List<?> list;
    }

    @Test
    public void testObjectList() throws Exception {
        HtmlAdapter<WildcardModel> htmlAdapter = Jspoon.create().adapter(WildcardModel.class);
        WildcardModel model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertNotNull(model.list);
        assertFalse(model.list.isEmpty());
        assertEquals("123", model.list.get(0));
    }

    static class BoundWildcardModel {
        @Selector(".number") List<? extends String> list;
    }

    @Test
    public void testStringList() throws Exception {
        HtmlAdapter<BoundWildcardModel> htmlAdapter = Jspoon.create().adapter(BoundWildcardModel.class);
        BoundWildcardModel model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertNotNull(model.list);
        assertFalse(model.list.isEmpty());
        assertEquals("123", model.list.get(0));
    }

    static class ModelGeneric<T> {
        @SuppressWarnings("rawtypes")
        @Selector("div > div") List justList;
        @Selector("span") Collection<?> listAny;
        @Selector(".float") Set<? extends T> listExtType;
        @Selector(".negative") LinkedHashSet<? super T> listSupType;
        @Selector(".positive") LinkedList<T> listType;
    }

    static class ModelString extends ModelGeneric<String> {
    }
    static class ModelBigDecimal extends ModelGeneric<BigDecimal> {
    }

    @Test
    public void genericFieldsTest() throws Exception {
        @SuppressWarnings("rawtypes")
        HtmlAdapter<ModelGeneric> htmlAdapter = jspoon.adapter(ModelGeneric.class);
        ModelGeneric<?> model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertListEquals(model.justList, "3366", "-4", "9.98");
        assertListEquals(model.listAny, "123", "-200", "3.14");
        assertListEquals(model.listExtType, "3.14", "9.98", "2.76");
        assertListEquals(model.listSupType, "-200", "-4", "-1");
        assertListEquals(model.listType, "123", "3366", "1314");
    }

    @Test
    public void genericStringFieldsTest() throws Exception {
        HtmlAdapter<ModelString> htmlAdapter = jspoon.adapter(ModelString.class);
        ModelString model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertListEquals(model.justList, "3366", "-4", "9.98");
        assertListEquals(model.listAny, "123", "-200", "3.14");
        assertListEquals(model.listExtType, "3.14", "9.98", "2.76");
        assertListEquals(model.listSupType, "-200", "-4", "-1");
        assertListEquals(model.listType, "123", "3366", "1314");
    }

    @Test
    public void genericBigDecimalFieldsTest() throws Exception {
        HtmlAdapter<ModelBigDecimal> htmlAdapter = jspoon.adapter(ModelBigDecimal.class);
        ModelBigDecimal model = htmlAdapter.fromHtml(HTML_CONTENT);
        assertListEquals(model.justList, "3366", "-4", "9.98");
        assertListEquals(model.listAny, "123", "-200", "3.14");
        assertListEquals(model.listExtType, new BigDecimal("3.14"), new BigDecimal("9.98"), new BigDecimal("2.76"));
        assertListEquals(model.listSupType, new BigDecimal("-200"), new BigDecimal("-4"), new BigDecimal("-1"));
        assertListEquals(model.listType, new BigDecimal("123"), new BigDecimal("3366"), new BigDecimal("1314"));
    }

    private static void assertListEquals(Collection<?> actual, Object...expected) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        if (expected.length == 0) {
            assertTrue(actual != null && actual.isEmpty());
            return;
        }
        assertEquals(expected.length, actual.size());
        for (int i=0; i < expected.length; i++) {
            assertEquals(expected[i], getFromCollection(actual, i));
        }
    }

    private static Object getFromCollection(Collection<?> collection, int index) {
        Iterator<?> it = collection.iterator();
        for (int i=0; i < index; i++) {
            it.next();
        }
        return it.next();
    }
}