package pl.droidsonroids.jspoon;

import static org.junit.Assert.assertEquals;

import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import pl.droidsonroids.jspoon.annotation.Selector;

public class ConverterTest {

    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class WeatherReport {

        @Selector(value="#today-weather", converter=DayOfWeekConverter.class)
        DayOfWeek dayOfWeek;

        @Selector(value="#today-weather", converter=WeatherConverter.class)
        Weather weather;
    }

    private enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    private static class Weather {
        String condition;

        Weather(String condition) {
            this.condition = condition;
        }
    }

    private static class DayOfWeekConverter implements ElementConverter<DayOfWeek> {

        @Override
        public DayOfWeek convert(Element node, Selector selector) {
            String text = node.text();
            text = text.substring("Today is ".length(), text.indexOf("."));
            for (DayOfWeek dayOfWeek : DayOfWeek.values())
                if (dayOfWeek.name().equalsIgnoreCase(text))
                    return dayOfWeek;
            throw new IllegalArgumentException("Unknown day of week: " + text);
        }
    }

    private static class WeatherConverter implements ElementConverter<Weather> {

        @Override
        public Weather convert(Element node, Selector selector) {
            String text = node.text();
            int offset = text.indexOf("Weather");
            offset += "Weather is ".length();
            text = text.substring(offset);

            return new Weather(text);
        }
    }

    @Test
    public void testCustomConverter() {
        HtmlAdapter<WeatherReport> htmlAdapter = jspoon.adapter(WeatherReport.class);
        WeatherReport weatherReport = htmlAdapter.fromHtml(
            "<p id=\"today-weather\">Today is Saturday. Weather is sunny</p>"
        );

        assertEquals(weatherReport.dayOfWeek, DayOfWeek.SATURDAY);
        assertEquals(weatherReport.weather.condition, "sunny");
    }

}
