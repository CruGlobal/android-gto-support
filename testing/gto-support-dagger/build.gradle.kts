plugins {
    id("dagger.hilt.android.plugin")
}

dependencies {
    implementation(libs.dagger)
    api(libs.dagger.hilt.android)

    api(libs.androidx.appcompat)

    kapt(libs.dagger.hilt.compiler)
}
