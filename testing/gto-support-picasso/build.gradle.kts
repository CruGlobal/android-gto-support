plugins {
    id("gto-support.android-testing-conventions")
}

android.namespace = "org.ccci.gto.android.common.testing.picasso"

dependencies {
    api(libs.picasso)

    implementation(libs.mockito.kotlin)
    compileOnly(libs.junit)
}
