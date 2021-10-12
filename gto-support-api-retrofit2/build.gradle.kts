plugins {
    id("com.android.library")
}

configureAndroidLibrary()

dependencies {
    implementation(project(":gto-support-compat"))

    api(libs.retrofit)
}
