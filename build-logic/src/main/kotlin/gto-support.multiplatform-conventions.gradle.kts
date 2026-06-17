plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover")
    id("ktlint-conventions")
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

configurePublishing()
