plugins {
    kotlin("plugin.parcelize")
}

dependencies {
    implementation(project(":gto-support-compat"))
    implementation(project(":gto-support-core"))
    implementation(project(":gto-support-util"))
}
