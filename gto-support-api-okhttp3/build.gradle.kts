plugins {
    id("gto-support.android-conventions")
}

dependencies {
    api(project(":gto-support-api-base"))
    implementation(project(":gto-support-okhttp3"))

    implementation(libs.okhttp3)
}
