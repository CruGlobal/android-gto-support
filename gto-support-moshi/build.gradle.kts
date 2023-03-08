plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.moshi"

dependencies {
    implementation(project(":gto-support-compat"))

    api(libs.moshi)
}
