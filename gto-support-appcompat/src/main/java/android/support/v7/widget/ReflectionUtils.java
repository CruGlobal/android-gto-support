package android.support.v7.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

final class ReflectionUtils {
    @Nullable
    static Field getDeclaredField(@NonNull final Class<?> type, @NonNull final String fieldName) {
        try {
            final Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (final NoSuchFieldException ignored) {
        }
        return null;
    }
}
