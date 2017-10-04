package pl.droidsonroids.jspoon;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.rule.CustomLocaleRule;

import static org.junit.Assert.assertEquals;

public class AdvancedTest {
    private final static String HTML_CONTENT = "<div>"
            + "<h1 id='quote'>Quote of the day: \"Blah, blah.\"</h1>"
            + "<div class='post' data-created='11:20 11.04.2017'>"
            + "<h2>Header1</h2>"
            + "<p>Content1</p>"
            + "<ul class='tags'>"
            + "<li>Tag1</li>"
            + "<li>Tag3</li>"
            + "<li>Tag4</li>"
            + "</ul>"
            + "</div>"
            + "<div class='post' data-created='11:10 9.04.2017'>"
            + "<h2>Header2</h2>"
            + "<p>Content2</p>"
            + "<ul class='tags'>"
            + "<li>Tag2</li>"
            + "<li>Tag3</li>"
            + "<li>Tag5</li>"
            + "</ul>"
            + "</div>"
            + "<div class='post' data-created='21:37 1.04.2017'>"
            + "<h2>Header3</h2>"
            + "<p>Content3</p>"
            + "<ul class='tags'>"
            + "<li>Tag1</li>"
            + "<li>Tag4</li>"
            + "<li>Tag7</li>"
            + "</ul>"
            + "</div>"
            + "</div>";
    private final static Locale CUSTOM_DEFAULT_LOCALE = Locale.US;
    private Jspoon jspoon;

    @Rule
    public CustomLocaleRule customLocaleRule = new CustomLocaleRule(CUSTOM_DEFAULT_LOCALE);

    @Before
    public void setUp() {
        jspoon = Jspoon.create();
    }

    @Test
    public void advancedTest1() throws Exception {
        HtmlAdapter<Page> htmlAdapter = jspoon.adapter(Page.class);
        Page page = htmlAdapter.fromHtml(HTML_CONTENT);

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, CUSTOM_DEFAULT_LOCALE);

        List<Post> posts = new ArrayList<>();
        posts.add(new Post(dateFormat.parse("Apr 11, 2017 11:20:00 AM"), "Header1", "Content1", "Tag1", "Tag3", "Tag4"));
        posts.add(new Post(dateFormat.parse("Apr 9, 2017 11:10:00 AM"), "Header2", "Content2", "Tag2", "Tag3", "Tag5"));
        posts.add(new Post(dateFormat.parse("Apr 1, 2017 9:37:00 PM"), "Header3", "Content3", "Tag1", "Tag4", "Tag7"));

        assertEquals(page.quote, "Blah, blah.");
        assertEquals(page.posts, posts);
    }

    @Test
    public void advancedTest2() throws Exception {
        HtmlAdapter<PageListWithoutAnnotation> htmlAdapter = jspoon.adapter(PageListWithoutAnnotation.class);
        PageListWithoutAnnotation page = htmlAdapter.fromHtml(HTML_CONTENT);

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, CUSTOM_DEFAULT_LOCALE);

        List<AnnotatedPost> posts = new ArrayList<>();
        posts.add(new AnnotatedPost(dateFormat.parse("Apr 11, 2017 11:20:00 AM"), "Header1", "Content1", "Tag1", "Tag3", "Tag4"));
        posts.add(new AnnotatedPost(dateFormat.parse("Apr 9, 2017 11:10:00 AM"), "Header2", "Content2", "Tag2", "Tag3", "Tag5"));
        posts.add(new AnnotatedPost(dateFormat.parse("Apr 1, 2017 9:37:00 PM"), "Header3", "Content3", "Tag1", "Tag4", "Tag7"));

        assertEquals(page.quote, "Blah, blah.");
        assertEquals(page.posts, posts);
    }

    @Test
    public void advancedTest3() throws Exception {
        HtmlAdapter<PageLFieldWithoutAnnotation> htmlAdapter = jspoon.adapter(PageLFieldWithoutAnnotation.class);
        PageLFieldWithoutAnnotation page = htmlAdapter.fromHtml(HTML_CONTENT);

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, CUSTOM_DEFAULT_LOCALE);
        
        Post firstPost = new AnnotatedPost(dateFormat.parse("Apr 11, 2017 11:20:00 AM"), "Header1", "Content1", "Tag1", "Tag3", "Tag4");

        assertEquals(page.quote, "Blah, blah.");
        assertEquals(page.firstPost, firstPost);
    }

    private static class Page {
        @Selector(value = "#quote",
                  format = "\"(.*)\"") String quote;
        @Selector(".post") List<Post> posts;
    }

    private static class PageListWithoutAnnotation {
        @Selector(value = "#quote",
                  format = "\"(.*)\"") String quote;
        List<AnnotatedPost> posts;
    }

    private static class PageLFieldWithoutAnnotation {
        @Selector(value = "#quote",
                  format = "\"(.*)\"") String quote;
        AnnotatedPost firstPost;
    }

    private static class Post {
        @Selector(value = ":root",
                  attr = "data-created",
                  format = "HH:mm dd.MM.yyyy") Date created;
        @Selector("h2") String header;
        @Selector("p") String content;
        @Selector("li") List<String> tags;

        Post() {
            //no-op
        }

        Post(Date created, String header, String content, String... tags) {
            this.created = created;
            this.header = header;
            this.content = content;
            this.tags = Arrays.asList(tags);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Post post = (Post) o;

            if (created != null ? !created.equals(post.created) : post.created != null) return false;
            if (header != null ? !header.equals(post.header) : post.header != null) return false;
            if (content != null ? !content.equals(post.content) : post.content != null) return false;
            return tags != null ? tags.equals(post.tags) : post.tags == null;
        }

        @Override
        public int hashCode() {
            int result = created != null ? created.hashCode() : 0;
            result = 31 * result + (header != null ? header.hashCode() : 0);
            result = 31 * result + (content != null ? content.hashCode() : 0);
            result = 31 * result + (tags != null ? tags.hashCode() : 0);
            return result;
        }
    }

    @Selector(".post")
    private static class AnnotatedPost extends Post {
        @Selector(value = ":root",
                  attr = "data-created",
                  format = "HH:mm dd.MM.yyyy") Date created;
        @Selector("h2") String header;
        @Selector("p") String content;
        @Selector("li") ArrayList<String> tags;

        AnnotatedPost() {
            //no-op
        }

        AnnotatedPost(Date created, String header, String content, String... tags) {
            this.created = created;
            this.header = header;
            this.content = content;
            this.tags = new ArrayList<>(Arrays.asList(tags));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AnnotatedPost post = (AnnotatedPost) o;

            if (created != null ? !created.equals(post.created) : post.created != null) return false;
            if (header != null ? !header.equals(post.header) : post.header != null) return false;
            if (content != null ? !content.equals(post.content) : post.content != null) return false;
            return tags != null ? tags.equals(post.tags) : post.tags == null;
        }

        @Override
        public int hashCode() {
            int result = created != null ? created.hashCode() : 0;
            result = 31 * result + (header != null ? header.hashCode() : 0);
            result = 31 * result + (content != null ? content.hashCode() : 0);
            result = 31 * result + (tags != null ? tags.hashCode() : 0);
            return result;
        }
    }
}
