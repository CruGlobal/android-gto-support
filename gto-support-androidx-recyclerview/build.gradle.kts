plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.androidx.recyclerview)
    implementation(libs.weakdelegate)

    // region *DataBindingAdapter
    compileOnly(libs.androidx.databinding.runtime)
    // endregion *DataBindingAdapter

    testImplementation(libs.junitParams)
}
