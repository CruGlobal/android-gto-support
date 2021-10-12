plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.androidx.work.runtime)

    compileOnly(libs.timber)
}
