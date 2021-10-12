plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.napier)

    compileOnly(libs.timber)
}
