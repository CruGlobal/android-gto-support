plugins {
    id("gto-support.java-conventions")
}

dependencies {
    api(project(":gto-support-jsonapi"))

    api(libs.retrofit)
    implementation(libs.androidx.annotation)
    implementation(libs.json)

    testImplementation(libs.jsonUnit)
    testImplementation(libs.jsonUnit.fluent)
    testImplementation(libs.okhttp3.mockwebserver)
}
