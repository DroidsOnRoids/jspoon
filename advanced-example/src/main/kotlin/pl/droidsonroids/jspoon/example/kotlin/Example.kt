package pl.droidsonroids.jspoon.example.kotlin

import io.reactivex.Single
import pl.droidsonroids.jspoon.annotation.Selector
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class BlogPage {
    @Selector(".post") lateinit var posts: List<Post>
}

class Post {
    @Selector(".post-content > h2 > a") lateinit var title: String
    @Selector(".excerpt") lateinit var excerpt: String
    @Selector(".post-featured-image > a > img", attr = "data-lazy-src") lateinit var imageUrl: String
    @Selector(".post-category > a") lateinit var tags: List<String>
}

interface BlogService {
    @GET("blog")
    fun getBlogPage(@Query("page") pageNumber: Int): Single<BlogPage>
}

fun main(args: Array<String>) {
    createRetrofit()
            .create(BlogService::class.java)
            .getBlogPage(1)
            .subscribe { blog ->
                blog.posts.forEach {
                    println(it.title)
                    println(it.excerpt)
                    println(it.imageUrl)
                    println(it.tags.joinToString())
                    println()
                }
            }
}

fun createRetrofit(): Retrofit =
        Retrofit.Builder()
                .baseUrl("https://www.thedroidsonroids.com/")
                .addConverterFactory(JspoonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()