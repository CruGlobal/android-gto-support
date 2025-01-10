plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.firebase.remoteconfig"

dependencies {
    implementation(libs.firebase.config)
}
