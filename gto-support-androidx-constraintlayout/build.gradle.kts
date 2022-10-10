plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.constraintlayout"
    baseConfiguration(project)
    buildFeatures.dataBinding = true
    dataBinding.addDefaultAdapters = false
}

dependencies {
    api(libs.androidx.constraintlayout)

    // region DataBinding dependencies
    compileOnly(libs.androidx.databinding.adapters)
    compileOnly(libs.androidx.databinding.runtime)
    // endregion DataBinding dependencies
}
