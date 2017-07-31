package pl.droidsonroids.jspoon.example.java;

import pl.droidsonroids.jspoon.example.java.api.BlogService;
import pl.droidsonroids.jspoon.example.java.model.BlogPage;
import pl.droidsonroids.jspoon.example.java.model.Post;
import pl.droidsonroids.retrofit2.JspoonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class Example {
    public static void main(String args[]) {
        BlogService blogService = createBlogService();
        blogService.getBlogPage(1)
                .subscribe(Example::printBlogPage);
    }

    private static BlogService createBlogService() {
        return createRetrofit()
                .create(BlogService.class);
    }

    private static Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://www.thedroidsonroids.com/")
                .addConverterFactory(JspoonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private static void printBlogPage(BlogPage blogPage) {
        blogPage.posts.forEach(Example::printPost);
    }

    private static void printPost(Post post) {
        System.out.println(post.title);
        System.out.println(post.imageUrl);
        System.out.println(String.join(", ", post.tags));
        System.out.println();
    }
}