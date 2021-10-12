plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(libs.leakcanary)

    compileOnly(libs.firebase.crashlytics)
    compileOnly(libs.timber)
}
