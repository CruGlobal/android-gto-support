plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(project(":gto-support-jsonapi"))
    implementation(project(":gto-support-scarlet"))

    api(libs.scarlet.core)

    testImplementation(libs.json)
}
