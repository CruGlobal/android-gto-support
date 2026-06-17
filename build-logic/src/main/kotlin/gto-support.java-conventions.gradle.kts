import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover")
    id("ktlint-conventions")
}

kotlin {
    configureJvmToolchain(project)
}

configurePublishing()
