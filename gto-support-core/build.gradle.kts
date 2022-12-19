plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common"
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(project(":gto-support-androidx-collection"))
    implementation(project(":gto-support-base"))
    implementation(project(":gto-support-compat"))
    implementation(project(":gto-support-util"))

    implementation(libs.androidx.fragment)
}
