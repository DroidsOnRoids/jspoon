package pl.droidsonroids.jspoon;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import pl.droidsonroids.jspoon.annotation.Format;
import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.rule.CustomLocaleRule;

public class BigDecimalFormatTest {

    private Jspoon jspoon;

    @Rule
    public CustomLocaleRule customLocaleRule = new CustomLocaleRule(Locale.US);

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class Money {
        @Selector(value = "#amount", format = "0,000.00") BigDecimal amount;

        @Format("0,000.00")
        @Selector(value = "#amount") BigDecimal amount2;
    }

    @Test
    public void amount() throws ParseException {
        Money money = createObjectFromHtml(Money.class);
        DecimalFormat format = new DecimalFormat("0,000.00");
        format.setParseBigDecimal(true);
        BigDecimal expected = (BigDecimal) format.parse("50,000.00");
        assertEquals(expected, money.amount);
        assertEquals(expected, money.amount2);
    }

    private <T> T createObjectFromHtml(Class<T> className) {
        HtmlAdapter<T> htmlAdapter = jspoon.adapter(className);
        return htmlAdapter.fromHtml("<div>"
                + "<span id='amount'>50,000.00</span>"
                + "</div>");
    }
}
