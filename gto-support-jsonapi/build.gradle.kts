plugins {
    id("gto-support.java-conventions")
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.json)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.jsonUnit)
    testImplementation(libs.jsonUnit.fluent)
}
