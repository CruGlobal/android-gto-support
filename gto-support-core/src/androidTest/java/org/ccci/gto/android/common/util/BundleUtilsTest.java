package org.ccci.gto.android.common.util;

import android.os.Bundle;

import org.junit.Test;

import java.util.Locale;

import static org.ccci.gto.android.common.util.BundleUtils.getEnum;
import static org.ccci.gto.android.common.util.BundleUtils.getLocale;
import static org.ccci.gto.android.common.util.BundleUtils.putEnum;
import static org.ccci.gto.android.common.util.BundleUtils.putLocale;
import static org.ccci.gto.android.common.util.BundleUtilsTest.TestEnum.DEFVALUE;
import static org.ccci.gto.android.common.util.BundleUtilsTest.TestEnum.VALUE1;
import static org.ccci.gto.android.common.util.BundleUtilsTest.TestEnum.VALUE2;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public final class BundleUtilsTest {
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String KEY3 = "key3";

    enum TestEnum {
        VALUE1,
        VALUE2,
        DEFVALUE
    }

    @Test
    public void verifyPutEnum() throws Exception {
        final Bundle bundle = new Bundle();
        putEnum(bundle, KEY1, VALUE1);
        putEnum(bundle, KEY2, VALUE2);
        putEnum(bundle, KEY3, null);

        assertThat(bundle.size(), is(3));
        assertThat(bundle.getString(KEY1), is(VALUE1.name()));
        assertThat(bundle.getString(KEY2), is(VALUE2.name()));
        assertThat(bundle.getString(KEY3), is(nullValue()));
    }

    @Test
    public void verifyGetEnum() throws Exception {
        final Bundle bundle = new Bundle();
        putEnum(bundle, KEY1, VALUE1);
        putEnum(bundle, KEY2, VALUE2);
        bundle.putString(KEY3, VALUE1.name() + "-3");

        assertThat(getEnum(bundle, TestEnum.class, KEY1), is(VALUE1));
        assertThat(getEnum(bundle, TestEnum.class, KEY2), is(VALUE2));
        assertThat(getEnum(bundle, TestEnum.class, KEY3), nullValue());
    }

    @Test
    public void verifyGetEnumDefault() throws Exception {
        final Bundle bundle = new Bundle();
        putEnum(bundle, KEY1, VALUE1);
        bundle.putString(KEY2, VALUE2.name() + "-3");

        assertThat(getEnum(bundle, TestEnum.class, KEY1, DEFVALUE), is(VALUE1));
        assertThat(getEnum(bundle, TestEnum.class, KEY2, DEFVALUE), is(DEFVALUE));
        assertThat(getEnum(bundle, TestEnum.class, KEY3, DEFVALUE), is(DEFVALUE));
    }

    @Test
    public void verifyPutLocaleAndGetLocale() throws Exception {
        final Bundle bundle = new Bundle();
        putLocale(bundle, KEY1, Locale.ENGLISH);
        putLocale(bundle, KEY2, Locale.FRENCH);
        putLocale(bundle, KEY3, null);

        assertThat(bundle.size(), is(3));
        assertThat(getLocale(bundle, KEY1), is(Locale.ENGLISH));
        assertThat(getLocale(bundle, KEY2), is(Locale.FRENCH));
        assertThat(getLocale(bundle, KEY3), is(nullValue()));
    }
}
