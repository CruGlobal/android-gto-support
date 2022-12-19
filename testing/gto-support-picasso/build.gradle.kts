plugins {
    id("gto-support.android-testing-conventions")
}

dependencies {
    api(libs.picasso)

    implementation(libs.mockito.kotlin)
    compileOnly(libs.junit)
}
