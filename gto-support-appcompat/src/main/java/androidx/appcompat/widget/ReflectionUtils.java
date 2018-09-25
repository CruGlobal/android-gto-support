package androidx.appcompat.widget;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class ReflectionUtils {
    @Nullable
    public static Field getDeclaredField(@NonNull final Class<?> type, @NonNull final String fieldName) {
        try {
            final Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (final NoSuchFieldException ignored) {
        }
        return null;
    }
}
