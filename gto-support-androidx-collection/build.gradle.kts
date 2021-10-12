plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.parcelize")
}

configureAndroidLibrary()

dependencies {
    api(libs.androidx.collection)

    testImplementation(libs.androidx.collection.ktx)
}
