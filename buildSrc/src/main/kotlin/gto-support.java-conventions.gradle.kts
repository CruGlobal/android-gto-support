import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jmailen.kotlinter")
}

kotlin {
    configureJvmToolchain(project)
}

configureKotlinKover()
configureKotlinter()
configurePublishing()
