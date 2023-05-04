plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("org.jmailen.kotlinter")
}

android {
    baseConfiguration(project)
}

kotlin {
    baseConfiguration()
    configureJvmToolchain(project)
    configureTargets(project)
}

configureKotlinter()
configurePublishing()
