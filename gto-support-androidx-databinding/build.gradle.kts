plugins {
    id("gto-support.android-conventions")
    kotlin("kapt")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.databinding"
    buildFeatures.dataBinding = true
}

dependencies {
    implementation(project(":gto-support-util"))

    compileOnly(libs.androidx.core)
}
