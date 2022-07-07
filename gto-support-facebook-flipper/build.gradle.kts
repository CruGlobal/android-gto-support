plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()
android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    implementation(libs.facebookFlipper)

    compileOnly(libs.androidx.sqlite)
}
