plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(libs.firebase.crashlytics)

    compileOnly(libs.timber)
}
