package pl.droidsonroids.jspoon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.rule.CustomLocaleRule;

import static org.junit.Assert.assertEquals;

public class SimpleTypes {
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
    private Jspoon jspoon;

    @ClassRule
    public static CustomLocaleRule defaultLocale = new CustomLocaleRule(Locale.US);

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
        HtmlAdapter<BooleanModel> htmlAdapter = jspoon.adapter(BooleanModel.class);
        BooleanModel booleanModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(booleanModel.testBoolean1, true);
        assertEquals(booleanModel.testBoolean2, false);
        assertEquals(booleanModel.testBoolean3, false);
    }

    @Test
    public void integerTest() {
        HtmlAdapter<IntModel> htmlAdapter = jspoon.adapter(IntModel.class);
        IntModel intModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(intModel.testInt1, -200);
        assertEquals(intModel.testInt2, 4);
        assertEquals(intModel.testInteger3, Integer.valueOf(32000));
    }

    @Test
    public void longTest() {
        HtmlAdapter<LongModel> htmlAdapter = jspoon.adapter(LongModel.class);
        LongModel longModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(longModel.testLong1, -200);
        assertEquals(longModel.testLong2, 4);
        assertEquals(longModel.testLong3, Long.valueOf(32000));
    }

    @Test
    public void floatTest() {
        HtmlAdapter<FloatModel> htmlAdapter = jspoon.adapter(FloatModel.class);
        FloatModel floatModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(floatModel.testFloat1, 4.1, 0.01);
        assertEquals(floatModel.testFloat2, -10.00001, 0.01);
        assertEquals(floatModel.testFloat3, -32.123456, 0.01);
    }

    @Test
    public void doubleTest() {
        HtmlAdapter<DoubleModel> htmlAdapter = jspoon.adapter(DoubleModel.class);
        DoubleModel doubleModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(doubleModel.testDouble1, 4.1, 0.000001);
        assertEquals(doubleModel.testDouble2, -10.00001, 0.000001);
        assertEquals(doubleModel.testDouble3, -32.123456, 0.000001);
    }

    @Test
    public void stringTest() {
        HtmlAdapter<StringModel> htmlAdapter = jspoon.adapter(StringModel.class);
        StringModel stringModel = htmlAdapter.fromHtml(HTML_CONTENT);
        assertEquals(stringModel.testString1, "Test1");
        assertEquals(stringModel.testString2, "");
        assertEquals(stringModel.testString3, "Test2 ".trim());
    }

    @Test
    public void elementTest() {
        HtmlAdapter<ElementModel> htmlAdapter = jspoon.adapter(ElementModel.class);
        ElementModel elementModel = htmlAdapter.fromHtml(HTML_CONTENT);
        Element testElement = elementModel.testElement;
        assertEquals(testElement.id(), "element-test");
        assertEquals(testElement.attr("data-test"), "test");
    }

    @Test
    public void dateTest() throws Exception {
        HtmlAdapter<DateModel> htmlAdapter = jspoon.adapter(DateModel.class);
        DateModel dateModel = htmlAdapter.fromHtml(HTML_CONTENT);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        assertEquals(dateModel.testDate1, simpleDateFormat.parse("14.07.2017"));
        assertEquals(dateModel.testDate2, simpleDateFormat.parse("01.04.2137"));
        assertEquals(dateModel.testDate3, simpleDateFormat.parse("30.07.1444"));
    }
}