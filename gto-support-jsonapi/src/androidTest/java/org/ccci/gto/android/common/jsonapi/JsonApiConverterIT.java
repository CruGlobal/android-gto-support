package org.ccci.gto.android.common.jsonapi;

import android.support.test.runner.AndroidJUnit4;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class JsonApiConverterIT {
    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterNoType() throws Exception {
        new JsonApiConverter(ModelNoType.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterDuplicateTypes() throws Exception {
        new JsonApiConverter(ModelDuplicateType1.class, ModelDuplicateType2.class);
    }

    public static final class ModelNoType {}

    @JsonApiType("type")
    public static final class ModelDuplicateType1 {}

    @JsonApiType("type")
    public static final class ModelDuplicateType2 {}
}
