plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.recyclerview"
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(project(":gto-support-androidx-recyclerview"))

    api(libs.androidx.recyclerview)

    testImplementation(libs.junitParams)
}
