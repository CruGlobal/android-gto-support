plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.util"
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    implementation(project(":gto-support-compat"))

    implementation(libs.timber)

    testImplementation(libs.json)
    testImplementation(libs.jsonUnit)
}
