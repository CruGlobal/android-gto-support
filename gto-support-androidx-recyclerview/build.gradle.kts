plugins {
    id("gto-support.android-conventions")
    kotlin("kapt")
}

android {
    buildFeatures.dataBinding = true
    dataBinding.addDefaultAdapters = false
}

dependencies {
    api(libs.androidx.recyclerview)
    implementation(libs.weakdelegate)

    // region Data Binding
    compileOnly(libs.androidx.databinding.runtime)
    compileOnly(libs.androidx.databinding.adapters)
    // endregion Data Binding

    testImplementation(libs.junitParams)
}
