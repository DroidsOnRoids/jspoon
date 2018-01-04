package pl.droidsonroids.jspoon;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.rule.CustomLocaleRule;

import static org.junit.Assert.assertEquals;

public class SimpleTypesTest {
    private final static String HTML_CONTENT = "<div>"
            + "<span id='string1'>Test1</span>"
            + "<span id='int1'>-200</span>"
            + "<span id='float1'>4.1</span>"
            + "<span id='boolean1'>true</span>"
            + "<span id='date1'>Jul 14, 2017</span>"
            + "<div id='string2'></div>"
            + "<div id='int2'>4</div>"
            + "<div id='float2'>-10.00001</div>"
            + "<div id='boolean2'>false</div>"
            + "<div id='date2'>Apr 1, 2137</div>"
            + "<p id='string3'>Test2 </p>"
            + "<p id='int3'>32000</p>"
            + "<p id='float3'>-32.123456</p>"
            + "<p id='boolean3'>test</p>"
            + "<p id='date3'>Jul 30, 1444</p>"
            + "<div id='element-test' data-test='test'/>"
            + "</div>";
    private final static Locale CUSTOM_DEFAULT_LOCALE = Locale.US;
    private Jspoon jspoon;

    @ClassRule
    public static CustomLocaleRule customLocaleRule = new CustomLocaleRule(CUSTOM_DEFAULT_LOCALE);

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class BooleanModel {
        @Selector("#boolean1") boolean testBoolean1;
        @Selector("#boolean2") boolean testBoolean2;
        @Selector("#boolean3") Boolean testBoolean3;
    }

    private static class IntModel {
        @Selector("#int1") int testInt1;
        @Selector("#int2") int testInt2;
        @Selector("#int3") Integer testInteger3;
    }

    private static class LongModel {
        @Selector("#int1") long testLong1;
        @Selector("#int2") long testLong2;
        @Selector("#int3") Long testLong3;
    }

    private static class FloatModel {
        @Selector("#float1") float testFloat1;
        @Selector("#float2") float testFloat2;
        @Selector("#float3") Float testFloat3;
    }

    private static class DoubleModel {
        @Selector("#float1") double testDouble1;
        @Selector("#float2") double testDouble2;
        @Selector("#float3") Double testDouble3;
    }

    private static class StringModel {
        @Selector("#string1") String testString1;
        @Selector("#string2") String testString2;
        @Selector("#string3") String testString3;
    }

    private static class ElementModel {
        @Selector("#element-test") Element testElement;
    }

    private static class DateModel {
        @Selector("#date1") Date testDate1;
        @Selector("#date2") Date testDate2;
        @Selector("#date3") Date testDate3;
    }

    @Test
    public void booleanTest() {
        BooleanModel booleanModel = createObjectFromHtml(BooleanModel.class);
        assertEquals(booleanModel.testBoolean1, true);
        assertEquals(booleanModel.testBoolean2, false);
        assertEquals(booleanModel.testBoolean3, false);
    }

    @Test
    public void integerTest() {
        IntModel intModel = createObjectFromHtml(IntModel.class);
        assertEquals(intModel.testInt1, -200);
        assertEquals(intModel.testInt2, 4);
        assertEquals(intModel.testInteger3, Integer.valueOf(32000));
    }

    @Test
    public void longTest() {
        LongModel longModel = createObjectFromHtml(LongModel.class);
        assertEquals(longModel.testLong1, -200);
        assertEquals(longModel.testLong2, 4);
        assertEquals(longModel.testLong3, Long.valueOf(32000));
    }

    @Test
    public void floatTest() {
        FloatModel floatModel = createObjectFromHtml(FloatModel.class);
        assertEquals(floatModel.testFloat1, 4.1, 0.01);
        assertEquals(floatModel.testFloat2, -10.00001, 0.01);
        assertEquals(floatModel.testFloat3, -32.123456, 0.01);
    }

    @Test
    public void doubleTest() {
        DoubleModel doubleModel = createObjectFromHtml(DoubleModel.class);
        assertEquals(doubleModel.testDouble1, 4.1, 0.000001);
        assertEquals(doubleModel.testDouble2, -10.00001, 0.000001);
        assertEquals(doubleModel.testDouble3, -32.123456, 0.000001);
    }

    @Test
    public void stringTest() {
        StringModel stringModel = createObjectFromHtml(StringModel.class);
        assertEquals(stringModel.testString1, "Test1");
        assertEquals(stringModel.testString2, "");
        assertEquals(stringModel.testString3, "Test2 ".trim());
    }

    @Test
    public void elementTest() {
        ElementModel elementModel = createObjectFromHtml(ElementModel.class);
        Element testElement = elementModel.testElement;
        assertEquals(testElement.id(), "element-test");
        assertEquals(testElement.attr("data-test"), "test");
    }

    @Test
    public void dateTest() throws Exception {
        DateModel dateModel = createObjectFromHtml(DateModel.class);
        DateFormat defaultDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, CUSTOM_DEFAULT_LOCALE);
        assertEquals(dateModel.testDate1, defaultDateFormat.parse("Jul 14, 2017"));
        assertEquals(dateModel.testDate2, defaultDateFormat.parse("Apr 1, 2137"));
        assertEquals(dateModel.testDate3, defaultDateFormat.parse("Jul 30, 1444"));
    }

    private <T> T createObjectFromHtml(Class<T> className) {
        HtmlAdapter<T> htmlAdapter = jspoon.adapter(className);
        return htmlAdapter.fromHtml(HTML_CONTENT);
    }
}