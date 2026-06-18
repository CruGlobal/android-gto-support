plugins {
    id("gto-support.android-conventions")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    buildFeatures.compose = true
}

dependencies {
    // the runtime dependency is required to build a library when compose is enabled
    implementation(versionCatalog.findLibrary("androidx-compose-runtime").get())

    // these dependencies are required for tests of Composables
    debugImplementation(versionCatalog.findBundle("androidx-compose-debug").get())
    testDebugImplementation(versionCatalog.findBundle("androidx-compose-testing").get())
}
