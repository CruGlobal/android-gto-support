plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.firebase.crashlytics"

dependencies {
    implementation(libs.firebase.crashlytics)

    compileOnly(libs.timber)
}
