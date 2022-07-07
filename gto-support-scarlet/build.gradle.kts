plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()
android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(libs.scarlet.core)
    compileOnly(libs.scarlet)

    implementation(libs.okio)
}
