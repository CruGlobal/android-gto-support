plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.parcelize")
}

configureAndroidLibrary()

dependencies {
    implementation(project(":gto-support-compat"))
    implementation(project(":gto-support-core"))
    implementation(project(":gto-support-util"))

    implementation(libs.androidx.collection)
    implementation(libs.androidx.core)
}
