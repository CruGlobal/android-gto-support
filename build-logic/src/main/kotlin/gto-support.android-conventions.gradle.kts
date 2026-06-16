plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    baseConfiguration(project)
}

kotlin {
    configureJvmToolchain(project)
}

dependencies {
    testImplementation(versionCatalog.findBundle("android-test-framework").get())
}

configureKtlint()
configurePublishing()
