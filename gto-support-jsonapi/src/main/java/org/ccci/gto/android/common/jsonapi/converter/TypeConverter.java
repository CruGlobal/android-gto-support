package org.ccci.gto.android.common.jsonapi.converter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface TypeConverter<T> {
    boolean supports(@NonNull Class<?> clazz);

    @Nullable
    String toString(@Nullable T value);

    @Nullable
    T fromString(@Nullable String value);
}
