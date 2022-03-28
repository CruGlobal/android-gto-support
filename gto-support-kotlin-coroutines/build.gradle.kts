plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.kotlin.coroutines)

    // region LiveData extensions
    compileOnly(libs.androidx.lifecycle.livedata.core)
    // endregion LiveData extensions

    testImplementation(libs.kotlin.coroutines.test)
}
