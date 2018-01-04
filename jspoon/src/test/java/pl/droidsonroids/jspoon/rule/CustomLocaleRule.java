package pl.droidsonroids.jspoon.rule;

import java.util.Locale;
import org.junit.rules.ExternalResource;

public class CustomLocaleRule extends ExternalResource {
    private Locale customLocale;
    private Locale defaultLocale;

    public CustomLocaleRule(Locale customLocale) {
        this.customLocale = customLocale;
        defaultLocale = Locale.getDefault();
    }

    @Override
    protected void before() throws Throwable {
        Locale.setDefault(customLocale);
    }

    @Override
    protected void after() {
        Locale.setDefault(defaultLocale);
    }
}
