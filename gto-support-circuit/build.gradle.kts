plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.circuit"

    testFixtures.enable = true
}

dependencies {
    testFixturesImplementation(libs.circuit.overlay)
}
