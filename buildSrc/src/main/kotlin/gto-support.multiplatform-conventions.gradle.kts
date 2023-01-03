plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jmailen.kotlinter")
}

android {
    baseConfiguration(project)
}

kotlin {
    baseConfiguration()
    configureTargets(project)
}

configureKotlinter()
