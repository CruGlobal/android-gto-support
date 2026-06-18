plugins {
    id("gto-support.android-testing-conventions")
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ksp)
}

android.namespace = "org.ccci.gto.android.common.testing.dagger"

dependencies {
    implementation(libs.dagger)
    api(libs.dagger.hilt.android)

    api(libs.androidx.appcompat)

    ksp(libs.dagger.hilt.compiler)
}
