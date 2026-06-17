plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.facebook.flipper"
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    implementation(libs.facebookFlipper)

    compileOnly(libs.androidx.sqlite)
}

configurations.all {
    resolutionStrategy.force(libs.androidx.sqlite)
}
