plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(libs.facebookFlipper)

    compileOnly(libs.androidx.sqlite)
}
