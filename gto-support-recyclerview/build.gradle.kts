plugins {
    id("gto-support.android-conventions")
}

android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(project(":gto-support-androidx-recyclerview"))

    api(libs.androidx.recyclerview)

    // region *DataBindingAdapter
    compileOnly(libs.androidx.databinding.runtime)
    // endregion *DataBindingAdapter

    testImplementation(libs.junitParams)
}
