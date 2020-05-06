package org.ccci.gto.android.common.jsonapi.converter;

import org.junit.Test;

import java.io.Serializable;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class LocaleTypeConverterTest {
    @Test
    public void verifySupports() throws Exception {
        final LocaleTypeConverter converter = LocaleTypeConverter.INSTANCE;
        assertThat(converter.supports(Object.class), is(false));
        assertThat(converter.supports(Locale.class), is(true));
        assertThat(converter.supports(Cloneable.class), is(false));
        assertThat(converter.supports(Serializable.class), is(false));
    }

    @Test
    public void verifyToString() throws Exception {
        final LocaleTypeConverter converter = LocaleTypeConverter.INSTANCE;

        assertThat(converter.toString(Locale.ENGLISH), is("en"));
        assertThat(converter.toString(Locale.CANADA), is("en-CA"));
    }

    @Test
    public void verifyFromString() throws Exception {
        final LocaleTypeConverter converter = LocaleTypeConverter.INSTANCE;

        assertThat(converter.fromString("en"), is(Locale.ENGLISH));
        assertThat(converter.fromString("en-CA"), is(Locale.CANADA));
    }

    @Test
    public void verifyNull() throws Exception {
        final LocaleTypeConverter converter = LocaleTypeConverter.INSTANCE;

        assertThat(converter.fromString(null), nullValue());
        assertThat(converter.toString(null), nullValue());
    }
}
