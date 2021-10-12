plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    implementation(project(":gto-support-compat"))

    implementation(libs.timber)

    testImplementation(libs.json)
    testImplementation(libs.jsonUnit)
}
