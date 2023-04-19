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
    testImplementation(libs.json)
    testImplementation(libs.jsonUnit)
    testImplementation(libs.jsonUnit.fluent)
}
