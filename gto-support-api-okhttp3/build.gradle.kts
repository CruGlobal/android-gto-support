plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.api.okhttp3"

dependencies {
    api(project(":gto-support-api-base"))
    implementation(project(":gto-support-okhttp3"))

    implementation(libs.okhttp3)
}
