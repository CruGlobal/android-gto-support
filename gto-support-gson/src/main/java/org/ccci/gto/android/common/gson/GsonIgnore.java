package org.ccci.gto.android.common.gson;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @deprecated Since v3.3.0, we are favoring usage of Moshi in android apps over the usage of Gson.
 */
@Deprecated
@Retention(RUNTIME)
@Target({FIELD, TYPE})
public @interface GsonIgnore {
}
