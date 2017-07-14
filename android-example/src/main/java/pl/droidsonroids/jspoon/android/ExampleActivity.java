package pl.droidsonroids.jspoon.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import pl.droidsonroids.jspoon.HtmlAdapter;
import pl.droidsonroids.jspoon.Jspoon;
import pl.droidsonroids.jspoon.annotation.Selector;

public class ExampleActivity extends AppCompatActivity {
    private final static String HTML_EXAMPLE_STRING = "<div id='animals'><ul>"
            + "<li class='mammal'>dog</li>"
            + "<li class='fish'>salmon</li>"
            + "<li class='mammal'>cat</li>"
            + "<li class='mammal'>lion</li>"
            + "<li class='mammal'>tiger</li>"
            + "<li class='mammal'>elephant</li>"
            + "<li class='mammal'>giraffe</li>"
            + "<li class='bird'>eagle</li>"
            + "</ul></div>"
            + "<div id='not-animals'></div>";

    private static class Animals {
        @Selector("#animals") AnimalsDiv animalsDiv;
    }

    private static class AnimalsDiv {
        @Selector(".mammal") List<String> mammalList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        Jspoon jspoon = Jspoon.create();
        HtmlAdapter<Animals> htmlAdapter = jspoon.adapter(Animals.class);
        Animals animals = htmlAdapter.fromHtml(HTML_EXAMPLE_STRING);

        for (String mammal : animals.animalsDiv.mammalList) {
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.animal_text, linearLayout, false);
            textView.setText(mammal);
            linearLayout.addView(textView);
        }
    }
}
