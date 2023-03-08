plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.okhttp3"

dependencies {
    api(libs.okhttp3)
}
