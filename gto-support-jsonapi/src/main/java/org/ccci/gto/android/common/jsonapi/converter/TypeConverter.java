package org.ccci.gto.android.common.jsonapi.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface TypeConverter<T> {
    boolean supports(@NonNull Class<?> clazz);

    @Nullable
    String toString(@Nullable T value);

    @Nullable
    T fromString(@Nullable String value);
}
