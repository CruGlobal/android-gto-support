plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(project(":gto-support-jsonapi"))

    api(libs.retrofit)

    testImplementation(libs.json)
    testImplementation(libs.jsonUnit)
    testImplementation(libs.jsonUnit.fluent)
    testImplementation(libs.okhttp3.mockwebserver)
}
