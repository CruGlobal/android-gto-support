plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.leakcanary"
}

dependencies {
    implementation(libs.leakcanary)

    compileOnly(libs.firebase.crashlytics)
    compileOnly(libs.timber)
}
