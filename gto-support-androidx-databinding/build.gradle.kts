plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

configureAndroidLibrary()

android {
    buildFeatures.dataBinding = true
}

dependencies {
    implementation(project(":gto-support-util"))

    compileOnly(libs.androidx.core)
}
