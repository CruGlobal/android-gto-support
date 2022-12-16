plugins {
    id("gto-support.android-conventions")
}

dependencies {
    api(project(":gto-support-jsonapi"))
    implementation(project(":gto-support-scarlet"))

    api(libs.scarlet.core)

    testImplementation(libs.json)
}
