package pl.droidsonroids.retrofit2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import pl.droidsonroids.jspoon.HtmlAdapter;
import retrofit2.Converter;

class JspoonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final HttpUrl httpUrl;
    private final HtmlAdapter<T> htmlAdapter;

    JspoonResponseBodyConverter(HttpUrl httpUrl, HtmlAdapter<T> htmlAdapter) {
        this.httpUrl = httpUrl;
        this.htmlAdapter = htmlAdapter;
    }

    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        Charset charset = null;
        MediaType mediaType = responseBody.contentType();
        if (mediaType != null)
            charset = mediaType.charset();

        InputStream is = responseBody.byteStream();
        try {
            return htmlAdapter.fromInputStream(is, charset, httpUrl.url());
        } finally {
            is.close();
        }
    }
}
