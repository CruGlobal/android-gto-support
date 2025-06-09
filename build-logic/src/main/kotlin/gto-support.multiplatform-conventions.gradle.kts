plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    configureJvmToolchain(project)

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

configureKtlint()
configurePublishing()
