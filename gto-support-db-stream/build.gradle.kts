plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(project(":gto-support-db"))

    api(libs.lightweightStream)
}
