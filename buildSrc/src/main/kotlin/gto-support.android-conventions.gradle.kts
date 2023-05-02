plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jmailen.kotlinter")
}

android {
    baseConfiguration(project)
}

kotlin {
    configureJvmToolchain(project)
}

configureKotlinter()
