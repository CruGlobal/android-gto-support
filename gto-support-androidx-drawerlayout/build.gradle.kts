plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(project(":gto-support-util"))

    api(libs.androidx.drawerlayout)
}
