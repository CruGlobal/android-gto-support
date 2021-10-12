plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(project(":gto-support-compat"))

    implementation(libs.androidx.room.common)
}
