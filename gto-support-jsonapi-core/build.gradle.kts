plugins {
    id("gto-support.java-conventions")
}

dependencies {
    implementation(libs.androidx.annotation)
    compileOnly(libs.json)

    testImplementation(kotlin("test"))
    testImplementation(libs.json)
    testImplementation(libs.junit)
    testImplementation(libs.jsonUnit)
    testImplementation(libs.jsonUnit.fluent)
}
