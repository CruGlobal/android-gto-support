plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(libs.androidx.recyclerview)
    implementation(libs.weakdelegate)

    // region *DataBindingAdapter
    compileOnly(libs.androidx.databinding.runtime)
    // endregion *DataBindingAdapter
}
