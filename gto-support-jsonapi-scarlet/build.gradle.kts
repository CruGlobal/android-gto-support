plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.jsonapi.scarlet"

dependencies {
    api(project(":gto-support-jsonapi"))
    implementation(project(":gto-support-scarlet"))

    api(libs.scarlet.core)

    testImplementation(libs.json)
}
