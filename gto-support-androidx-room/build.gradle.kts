plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(libs.androidx.room.common)
}
