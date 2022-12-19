plugins {
    id("gto-support.android-conventions")
    kotlin("kapt")
}

android {
    buildFeatures.dataBinding = true
}

dependencies {
    implementation(project(":gto-support-util"))

    compileOnly(libs.androidx.core)
}
