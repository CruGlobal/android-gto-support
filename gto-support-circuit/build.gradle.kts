plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.circuit"

    testFixtures.enable = true
}

dependencies {
    // Deprecated: use org.ccci.gto.android.testing:gto-support-circuit directly.
    testFixturesApi(projects.testing.gtoSupportCircuit)
}
