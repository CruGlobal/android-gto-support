plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidTestingLibrary()

dependencies {
    api(libs.picasso)

    implementation(libs.mockitoKotlin)
    compileOnly(libs.junit)
}
