package com.tinder.scarlet.internal.utils;

import androidx.annotation.NonNull;

class RuntimePlatformInternals {
    /** @noinspection KotlinInternalInJava*/
    @NonNull
    @SuppressWarnings("MethodName")
    static RuntimePlatform Default() {
        return new RuntimePlatform.Default();
    }
}
