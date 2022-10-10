plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.scarlet"
    baseConfiguration(project)
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(libs.scarlet.core)
    compileOnly(libs.scarlet)

    implementation(libs.okio)
}
