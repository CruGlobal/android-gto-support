plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.leakcanary"
    baseConfiguration(project)
}

dependencies {
    implementation(libs.leakcanary)

    compileOnly(libs.firebase.crashlytics)
    compileOnly(libs.timber)
}
