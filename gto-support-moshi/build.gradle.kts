plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(project(":gto-support-compat"))

    api(libs.moshi)
}
