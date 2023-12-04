plugins {
    id("gto-support.android-conventions")
    kotlin("kapt")
}

android {
    namespace = "org.ccci.gto.android.common.material.components"
    defaultConfig.consumerProguardFile("proguard-consumer-tablayout.pro")

    buildFeatures.dataBinding = true
    dataBinding.addDefaultAdapters = false
}

dependencies {
    implementation(project(":gto-support-base"))
    implementation(project(":gto-support-util"))

    api(libs.materialComponents)

    // region Data Binding
    compileOnly(libs.androidx.databinding.adapters)
    compileOnly(libs.androidx.databinding.runtime)
    testImplementation(libs.androidx.databinding.runtime)
    // endregion Data Binding
}
