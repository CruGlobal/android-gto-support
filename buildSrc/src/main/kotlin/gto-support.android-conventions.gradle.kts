plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("org.jmailen.kotlinter")
}

android {
    baseConfiguration(project)
}

kotlin {
    configureJvmToolchain(project)
}

configureKotlinKover()
configureKotlinter()
configurePublishing()
