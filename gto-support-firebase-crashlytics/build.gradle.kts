plugins {
    id("gto-support.android-conventions")
}

dependencies {
    implementation(libs.firebase.crashlytics)

    compileOnly(libs.timber)
}
