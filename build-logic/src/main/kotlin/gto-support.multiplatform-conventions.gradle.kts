plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.kover")
    id("ktlint-conventions")
    id("publishing-conventions")
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
