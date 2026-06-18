import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
    id("ktlint-conventions")
    id("publishing-conventions")
}

kotlin {
    configureJvmToolchain(project)
}

baseDependencyResolutionStrategy()
