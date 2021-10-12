plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.androidx.fragment)
    compileOnly(libs.androidx.fragment.ktx)

    // DataBindingDialogFragment dependencies
    compileOnly(libs.androidx.appcompat)
    compileOnly(libs.androidx.databinding.runtime)
}
