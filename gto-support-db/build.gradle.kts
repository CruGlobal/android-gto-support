plugins {
    id("gto-support.android-conventions")
    kotlin("plugin.parcelize")
}

android.namespace = "org.ccci.gto.android.common.db"

dependencies {
    implementation(project(":gto-support-compat"))
    implementation(project(":gto-support-core"))
    implementation(project(":gto-support-util"))

    implementation(libs.androidx.collection)
    implementation(libs.androidx.core)
}
