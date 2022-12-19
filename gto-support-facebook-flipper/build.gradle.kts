plugins {
    id("gto-support.android-conventions")
}

android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    implementation(libs.facebookFlipper)

    compileOnly(libs.androidx.sqlite)
}
