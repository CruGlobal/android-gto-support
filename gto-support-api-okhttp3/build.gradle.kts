plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.api.okhttp3"

dependencies {
    api(project(":gto-support-api-base"))

    api(libs.okhttp3)
}
