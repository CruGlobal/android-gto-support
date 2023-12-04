plugins {
    id("gto-support.android-conventions")
    kotlin("kapt")
}

android {
    namespace = "org.ccci.gto.android.common.lottie"
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")

    buildFeatures.dataBinding = true
    dataBinding.addDefaultAdapters = false
}

dependencies {
    implementation(project(":gto-support-util"))

    api(libs.lottie)
    implementation(libs.androidx.appcompat)

    // region Data Binding
    compileOnly(libs.androidx.databinding.runtime)
    testImplementation(libs.androidx.databinding.runtime)
    // endregion Data Binding

    compileOnly(libs.okio)
}
