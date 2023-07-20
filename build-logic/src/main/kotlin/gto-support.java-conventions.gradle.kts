import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    configureJvmToolchain(project)
}

configureKtlint()
configurePublishing()
