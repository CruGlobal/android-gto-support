plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover")
    id("org.jmailen.kotlinter")
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

configureKotlinter()
configurePublishing()
