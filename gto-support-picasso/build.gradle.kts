plugins {
    id("gto-support.android-conventions")
    kotlin("kapt")
}

android {
    namespace = "org.ccci.gto.android.common.picasso"

    defaultConfig.consumerProguardFiles("proguard-consumer-requestcreator.pro")

    buildFeatures.dataBinding = true
    dataBinding.addDefaultAdapters = false
}

dependencies {
    api(libs.picasso)

    implementation(project(":gto-support-base"))
    implementation(project(":gto-support-compat"))
    implementation(project(":gto-support-util"))

    implementation(libs.timber)

    compileOnly(libs.androidx.core.ktx)

    // region Data Binding Adapters
    compileOnly(libs.androidx.databinding.runtime)
    testImplementation(libs.androidx.databinding.runtime)
    // endregion Data Binding Adapters

    // region Material Component Targets
    compileOnly(libs.materialComponents)
    // endregion Material Component Targets

    // region SimplePicassoImageView
    compileOnly(libs.androidx.appcompat)
    // endregion SimplePicassoImageView

    compileOnly(libs.kotlin.coroutines)

    testImplementation(project(":testing:gto-support-picasso"))
    testImplementation(libs.materialComponents)
    testImplementation(libs.kotlin.coroutines.android)
    testImplementation(libs.kotlin.coroutines.test)
}
