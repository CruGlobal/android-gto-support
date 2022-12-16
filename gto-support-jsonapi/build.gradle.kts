plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.jsonapi"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
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
