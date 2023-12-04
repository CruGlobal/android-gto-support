plugins {
    id("gto-support.android-conventions")
    kotlin("kapt")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.recyclerview"
    buildFeatures.dataBinding = true
    dataBinding.addDefaultAdapters = false
}

dependencies {
    api(libs.androidx.recyclerview)
    implementation(libs.weakdelegate)

    // region Data Binding
    compileOnly(libs.androidx.databinding.adapters)
    compileOnly(libs.androidx.databinding.runtime)
    testImplementation(libs.androidx.databinding.runtime)
    // endregion Data Binding

    testImplementation(libs.junitParams)
}
