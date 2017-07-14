package pl.droidsonroids.jspoon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.rule.CustomLocaleRule;

import static org.junit.Assert.assertEquals;

public class DateFormatTest {
    private final static String HTML_CONTENT = "<div>"
            + "<span id='default-date'>Jul 14, 2017</span>"
            + "<span id='pl-date'>2017-07-14</span>"
            + "<span id='year-date'>2017</span>"
            + "<span id='full-date'>13:30:12 14.07.2017</span>"
            + "</div>";
    private final static Locale CUSTOM_DEFAULT_LOCALE = Locale.US;
    private Jspoon jspoon;

    @Rule
    public CustomLocaleRule customLocaleRule = new CustomLocaleRule(CUSTOM_DEFAULT_LOCALE);

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class DefaultDateModel {
        @Selector("#default-date") Date date;
    }

    private static class PlDateModel {
        @Selector(value = "#pl-date") Date date;
    }

    private static class YearDateModel {
        @Selector(value = "#year-date", format = "yyyy") Date date;
    }

    private static class FullDateModel {
        @Selector(value = "#full-date", format = "HH:mm:ss dd.MM.yyyy") Date date;
    }

    @Test
    public void defaultDate() throws Exception {
        DefaultDateModel defaultDateModel = createObjectFromHtml(DefaultDateModel.class);
        DateFormat defaultDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, CUSTOM_DEFAULT_LOCALE);
        assertEquals(defaultDateModel.date, defaultDateFormat.parse("Jul 14, 2017"));
    }

    @Test
    public void localeDate() throws Exception {
        Locale.setDefault(Locale.forLanguageTag("pl"));
        PlDateModel plDateModel = createObjectFromHtml(PlDateModel.class);
        DateFormat defaultDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, CUSTOM_DEFAULT_LOCALE);
        assertEquals(plDateModel.date, defaultDateFormat.parse("Jul 14, 2017"));
    }

    @Test
    public void yearDate() throws Exception {
        YearDateModel yearDateModel = createObjectFromHtml(YearDateModel.class);
        DateFormat yearDateFormat = new SimpleDateFormat("yyyy", CUSTOM_DEFAULT_LOCALE);
        assertEquals(yearDateModel.date, yearDateFormat.parse("2017"));
    }

    @Test
    public void fullDate() throws Exception {
        FullDateModel fullDateModel = createObjectFromHtml(FullDateModel.class);
        DateFormat fullDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy", CUSTOM_DEFAULT_LOCALE);
        assertEquals(fullDateModel.date, fullDateFormat.parse("13:30:12 14.07.2017"));
    }

    private <T> T createObjectFromHtml(Class<T> className) {
        HtmlAdapter<T> htmlAdapter = jspoon.adapter(className);
        return htmlAdapter.fromHtml(HTML_CONTENT);
    }
}
