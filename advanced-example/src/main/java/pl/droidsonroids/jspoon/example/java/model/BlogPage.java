package pl.droidsonroids.jspoon.example.java.model;

import java.util.List;
import pl.droidsonroids.jspoon.annotation.Selector;

public class BlogPage {
    @Selector(".post") public List<Post> posts;
}
