plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.jsonapi"
    baseConfiguration(project)

    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    implementation(project(":gto-support-compat"))
    implementation(project(":gto-support-core"))

    implementation(libs.androidx.collection)

    testImplementation(libs.json)
    testImplementation(libs.jsonUnit)
    testImplementation(libs.jsonUnit.fluent)
}
