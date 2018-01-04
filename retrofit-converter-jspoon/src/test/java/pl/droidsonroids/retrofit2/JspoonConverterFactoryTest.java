package pl.droidsonroids.retrofit2;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class JspoonConverterFactoryTest {

    private interface NonScrapingService {

        @GET("/item/{id}")
        Call<Item> getItem(@Path("id") String id);
    }

    private class Item {

        private String id;
        private String name;

        private Item(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    public void testJspoonConverterAllowsRetrofitToChooseAnotherConverter() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("Ahoy matey!"));

        Converter mockConverter = mock(Converter.class);
        when(mockConverter.convert(any(ResponseBody.class)))
            .thenReturn(new Item("1", "Item 1"));

        Factory mockConverterFactory = mock(Factory.class);
        when(mockConverterFactory.responseBodyConverter(
            any(Type.class),
            any(Annotation[].class),
            any(Retrofit.class))).thenReturn(mockConverter);

        NonScrapingService service = new Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(JspoonConverterFactory.create())
            .addConverterFactory(mockConverterFactory)
            .build()
            .create(NonScrapingService.class);
        Item item = service.getItem("1").execute().body();
        assertEquals(item.id, "1");
        assertEquals(item.name, "Item 1");

        verify(mockConverter, atLeastOnce()).convert(any(ResponseBody.class));
    }
}
