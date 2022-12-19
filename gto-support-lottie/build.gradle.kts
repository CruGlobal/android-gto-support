plugins {
    id("gto-support.android-conventions")
    kotlin("kapt")
}

android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")

    buildFeatures.dataBinding = true
    dataBinding.addDefaultAdapters = false
}

dependencies {
    implementation(project(":gto-support-util"))

    api(libs.lottie)
    implementation(libs.androidx.appcompat)

    compileOnly(libs.androidx.databinding.runtime)
    compileOnly(libs.okio)
}
