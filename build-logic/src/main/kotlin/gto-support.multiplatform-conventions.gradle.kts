plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    baseConfiguration()
    configureJvmToolchain(project)
}

configureKtlint()
configurePublishing()
