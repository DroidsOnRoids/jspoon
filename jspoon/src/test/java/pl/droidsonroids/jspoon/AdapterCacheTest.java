package pl.droidsonroids.jspoon;

import org.junit.Before;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;

import static org.junit.Assert.assertEquals;

public class AdapterCacheTest {
    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    private static class DumbModel {
        @Selector("div") String div;
    }

    @Test
    public void adapterCache() {
        HtmlAdapter<DumbModel> htmlAdapterFirst = jspoon.adapter(DumbModel.class);
        HtmlAdapter<DumbModel> htmlAdapterSecond = jspoon.adapter(DumbModel.class);
        assertEquals(htmlAdapterFirst, htmlAdapterSecond);
    }
}
