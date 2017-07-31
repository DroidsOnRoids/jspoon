package pl.droidsonroids.jspoon.example.java.api;

import io.reactivex.Single;
import pl.droidsonroids.jspoon.example.java.model.BlogPage;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BlogService {
    @GET("blog")
    Single<BlogPage> getBlogPage(@Query("page") int page);
}