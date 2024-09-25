plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.scarlet"
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(libs.scarlet.core)
    compileOnly(libs.scarlet)

    implementation(project(":gto-support-util"))
    implementation(libs.okio)
}
