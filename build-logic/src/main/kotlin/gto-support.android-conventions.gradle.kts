plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.kotlinx.kover")
    id("ktlint-conventions")
    id("publishing-conventions")
}

android {
    baseConfiguration(project)
}

kotlin {
    configureJvmToolchain(project)
}

dependencies {
    compileOnly(versionCatalog.findLibrary("androidx-annotation").get())

    testImplementation(versionCatalog.findBundle("android-test-framework").get())
}

configureTestSharding()
