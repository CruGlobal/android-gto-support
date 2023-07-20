plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    baseConfiguration(project)
}

kotlin {
    baseConfiguration()
    configureJvmToolchain(project)
    configureTargets(project)
}

koverReport {
    defaults {
        mergeWith("debug")
    }
}

configureKtlint()
configurePublishing()
