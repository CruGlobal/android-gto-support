package org.ccci.gto.android.common.jsonapi.converter;

import org.junit.Test;

import java.sql.Time;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateTypeConverterTest {
    private static final String DEFAULT = "yyyy-MM-dd'T'HH:mm:ssX";

    @Test(expected = IllegalArgumentException.class)
    public void verifyConstructorInvalidPattern() throws Exception {
        new DateTypeConverter("'");
    }

    @Test
    public void verifySupports() throws Exception {
        final DateTypeConverter converter = new DateTypeConverter(DEFAULT);
        assertThat(converter.supports(Object.class), is(false));
        assertThat(converter.supports(Date.class), is(true));
        assertThat(converter.supports(Time.class), is(false));
    }

    @Test
    public void verifyToString() throws Exception {
        final DateTypeConverter converter = new DateTypeConverter(DEFAULT);

        assertThat(converter.toString(new Date(0)), is("1970-01-01T00:00:00Z"));
    }

    @Test
    public void verifyFromString() throws Exception {
        final DateTypeConverter converter = new DateTypeConverter(DEFAULT);

        assertThat(converter.fromString("1970-01-01T00:00:00Z"), is(new Date(0)));
    }
}
