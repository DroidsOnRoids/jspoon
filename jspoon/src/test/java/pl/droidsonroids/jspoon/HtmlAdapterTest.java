package pl.droidsonroids.jspoon;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import pl.droidsonroids.jspoon.annotation.Selector;

public class HtmlAdapterTest {

    private static final String HTML_CONTENT = "<div>"
        + "<span id='firstname'>John</span>"
        + "<span id='lastname'>Doe</span>"
        + "</div>";

    private static class Profile {

        @Selector(value = "#firstname") String firstname;
        @Selector(value = "#lastname") String lastname;
    }

    private Jspoon jspoon;

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    @Test
    public void testFromInputStream() throws IOException {
        HtmlAdapter<Profile> adapter = jspoon.adapter(Profile.class);
        URL baseUrl = URI.create( "http://localhost/profile" ).toURL();
        try (ByteArrayInputStream inputStream
            = new ByteArrayInputStream(HTML_CONTENT.getBytes())) {

            Profile profile = adapter.fromInputStream(inputStream, baseUrl);
            assertEquals("John", profile.firstname);
            assertEquals("Doe", profile.lastname);
        }
    }
}
