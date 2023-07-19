import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jmailen.kotlinter")
    id("org.jetbrains.kotlinx.kover")
}

kotlin {
    configureJvmToolchain(project)
}

configureKotlinter()
configurePublishing()
