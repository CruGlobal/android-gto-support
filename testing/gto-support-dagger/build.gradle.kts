plugins {
    id("gto-support.android-testing-conventions")
    alias(libs.plugins.dagger.hilt)
    kotlin("kapt")
}

dependencies {
    implementation(libs.dagger)
    api(libs.dagger.hilt.android)

    api(libs.androidx.appcompat)

    kapt(libs.dagger.hilt.compiler)
}
