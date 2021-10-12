plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
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
