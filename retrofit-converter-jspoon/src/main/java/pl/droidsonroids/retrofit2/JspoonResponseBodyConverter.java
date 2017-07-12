package pl.droidsonroids.retrofit2;

import java.io.IOException;
import okhttp3.ResponseBody;
import pl.droidsonroids.jspoon.HtmlAdapter;
import retrofit2.Converter;

class JspoonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private HtmlAdapter<T> htmlAdapter;

    JspoonResponseBodyConverter(HtmlAdapter<T> htmlAdapter) {
        this.htmlAdapter = htmlAdapter;
    }

    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        return htmlAdapter.fromHtml(responseBody.string());
    }
}
