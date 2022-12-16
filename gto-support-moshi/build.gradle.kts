plugins {
    id("gto-support.android-conventions")
}

dependencies {
    implementation(project(":gto-support-compat"))

    api(libs.moshi)
}
