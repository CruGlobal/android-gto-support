plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.scarlet.core)
    compileOnly(libs.scarlet)

    implementation(libs.okio)
}
