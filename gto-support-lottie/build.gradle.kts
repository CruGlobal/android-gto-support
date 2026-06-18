plugins {
    id("gto-support.android-conventions")
    alias(libs.plugins.legacy.kapt)
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
    implementation(libs.okio)

    // region Data Binding
    compileOnly(libs.androidx.databinding.runtime)
    testImplementation(libs.androidx.databinding.runtime)
    // endregion Data Binding
}
