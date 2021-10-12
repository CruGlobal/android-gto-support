plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidTestingLibrary()

dependencies {
    api(libs.okta)
}
