package org.ccci.gto.android.common.support.v4.util;

import androidx.annotation.NonNull;

import org.ccci.gto.android.common.util.Ids;

/**
 * @deprecated Since v3.11.0, use {@link Ids} from gto-support-util instead.
 */
@Deprecated
public class IdUtils {
    /**
     * @deprecated Since v3.11.0, use {@link Ids#lookup(long)} from gto-support-util instead.
     */
    @NonNull
    @Deprecated
    public static <T> T convertId(final long id) {
        return Ids.lookup(id);
    }

    /**
     * Convert an arbitrary object to a long id. The arbitrary objects are kept in an internal mapping table, so use
     * simple immutable objects only to prevent leaking memory.
     *
     * @deprecated Since v3.11.0, use {@link Ids#generate(Object)} from gto-support-util instead.
     * @param id  arbitrary object
     * @param <T> any simple immutable class
     * @return unique id for arbitrary object
     */
    @Deprecated
    public static <T> long convertId(@NonNull final T id) {
        return Ids.generate(id);
    }
}
