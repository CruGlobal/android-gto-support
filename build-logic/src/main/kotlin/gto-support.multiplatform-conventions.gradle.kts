plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    baseConfiguration()
    configureJvmToolchain(project)

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

configureKtlint()
configurePublishing()
