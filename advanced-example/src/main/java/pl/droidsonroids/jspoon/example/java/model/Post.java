package pl.droidsonroids.jspoon.example.java.model;

import java.util.List;
import pl.droidsonroids.jspoon.annotation.Selector;

public class Post {
    @Selector(".post-content > h2 > a") public String title;
    @Selector(value = ".post-featured-image > a > img", attr = "data-lazy-src") public String imageUrl;
    @Selector(".post-category > a") public List<String> tags;
}
