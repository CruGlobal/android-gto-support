plugins {
    id("com.android.library")
    alias(libs.plugins.dagger.hilt)
    kotlin("android")
    kotlin("kapt")
}

configureAndroidTestingLibrary()

dependencies {
    implementation(libs.dagger)
    api(libs.dagger.hilt.android)

    api(libs.androidx.appcompat)

    kapt(libs.dagger.hilt.compiler)
}
