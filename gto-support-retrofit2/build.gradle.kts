plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.retrofit)
//
//    implementation(project(":gto-support-compat"))
}
