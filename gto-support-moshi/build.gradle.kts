plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.moshi"

dependencies {
    api(libs.moshi)
}
