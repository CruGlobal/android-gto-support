plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.androidx.recyclerview)

    // region *DataBindingAdapter
    compileOnly(libs.androidx.databinding.runtime)
    // endregion *DataBindingAdapter
}
