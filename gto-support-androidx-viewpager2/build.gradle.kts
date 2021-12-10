plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

configureAndroidLibrary()

android {
    buildFeatures.dataBinding = true
    dataBinding.addDefaultAdapters = false
}

dependencies {
    api(libs.androidx.viewpager2)

    compileOnly(libs.androidx.databinding.adapters)
    compileOnly(libs.androidx.databinding.runtime)

    testImplementation(libs.androidx.arch.core.testing)
}
