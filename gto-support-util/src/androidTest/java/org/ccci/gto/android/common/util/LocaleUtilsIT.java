package org.ccci.gto.android.common.util;

import android.os.Build;

import org.ccci.gto.android.common.compat.util.LocaleCompat;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import androidx.test.runner.AndroidJUnit4;

import static java.util.Locale.CANADA_FRENCH;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class LocaleUtilsIT {
    private static final Locale MALAY = LocaleCompat.forLanguageTag("ms");
    private static final Locale BENGKULU = LocaleCompat.forLanguageTag("pse");

    @Test
    public void verifyGetFallback() throws Exception {
        assertThat(LocaleUtils.getFallback(Locale.US), is(ENGLISH));
        assertThat(LocaleUtils.getFallback(BENGKULU), is(MALAY));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Extensions are currently not supported before lollipop
            assertThat(LocaleUtils.getFallback(LocaleCompat.forLanguageTag("en-x-private")), is(ENGLISH));
        }
    }

    @Test
    public void verifyGetFallbacks() throws Exception {
        assertThat(LocaleUtils.getFallbacks(Locale.US), equalTo(new Locale[] {Locale.US, ENGLISH}));
        assertThat(LocaleUtils.getFallbacks(ENGLISH), equalTo(new Locale[] {ENGLISH}));
        assertThat(LocaleUtils.getFallbacks(BENGKULU), equalTo(new Locale[] {BENGKULU, MALAY}));
    }

    @Test
    public void verifyGetFallbacksMulti() {
        // test batch fallback resolution
        assertThat(LocaleUtils.getFallbacks(Locale.US, ENGLISH), equalTo(new Locale[] {Locale.US, ENGLISH}));
        assertThat(LocaleUtils.getFallbacks(ENGLISH, Locale.US), equalTo(new Locale[] {ENGLISH, Locale.US}));
        assertThat(LocaleUtils.getFallbacks(Locale.US, Locale.CANADA, CANADA_FRENCH),
                   equalTo(new Locale[] {Locale.US, ENGLISH, Locale.CANADA, CANADA_FRENCH, FRENCH}));
    }
}
