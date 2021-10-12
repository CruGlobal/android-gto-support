plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.kotlin.coroutines)

    testImplementation(libs.kotlin.coroutines.test)
}
