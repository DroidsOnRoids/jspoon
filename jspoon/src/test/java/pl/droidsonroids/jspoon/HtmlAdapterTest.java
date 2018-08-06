package pl.droidsonroids.jspoon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.junit.Before;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;

import static org.junit.Assert.assertEquals;

public class HtmlAdapterTest {

    private static final String HTML_CONTENT = "<div>"
            + "<span id='firstName'>John</span>"
            + "<span id='lastName'>Doe</span>"
            + "</div>";

    private static class Profile {

        @Selector(value = "#firstName") String firstName;
        @Selector(value = "#lastName") String lastName;
    }

    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    @Test
    public void testFromInputStream() throws IOException {
        HtmlAdapter<Profile> adapter = jspoon.adapter(Profile.class);
        URL baseUrl = URI.create("http://localhost/profile").toURL();
        try (ByteArrayInputStream inputStream
                     = new ByteArrayInputStream(HTML_CONTENT.getBytes())) {

            Profile profile = adapter.fromInputStream(inputStream, baseUrl);
            assertEquals("John", profile.firstName);
            assertEquals("Doe", profile.lastName);
        }
    }
}
