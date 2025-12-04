plugins {
    id("gto-support.java-conventions")
}

dependencies {
    api(libs.moshi)

    testImplementation(kotlin("test"))
}
