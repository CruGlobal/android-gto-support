plugins {
    id("gto-support.android-conventions")
    alias(libs.plugins.legacy.kapt)
}

android {
    namespace = "org.ccci.gto.android.common.androidx.databinding"
    buildFeatures.dataBinding = true
}

dependencies {
    implementation(project(":gto-support-util"))

    compileOnly(libs.androidx.core)
}
