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

koverReport {
    defaults {
        mergeWith("debug")
    }
}

configureKtlint()
configurePublishing()
