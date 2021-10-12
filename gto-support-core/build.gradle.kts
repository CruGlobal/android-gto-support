plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    implementation(project(":gto-support-base"))
    implementation(project(":gto-support-compat"))
    implementation(project(":gto-support-util"))

    api(libs.androidx.loader)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.localbroadcastmanager)
    implementation(libs.androidx.swiperefreshlayout)
    compileOnly(libs.androidx.cursoradapter)
}
