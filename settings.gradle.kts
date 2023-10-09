pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

dependencyResolutionManagement {
    repositories {
        maven {
            setUrl("https://jitpack.io")
            content { includeGroupByRegex("com\\.github\\..*") }
        }
        maven {
            // This repository contains pre-release versions of the Compose Compiler
            url = uri("https://androidx.dev/storage/compose-compiler/repository/")
            content {
                includeGroup("androidx.compose.compiler")
            }
        }
        google()
        mavenCentral()
    }
}

// automatically accept the scans.gradle.com TOS when running in GHA
if (System.getenv("GITHUB_ACTIONS")?.toBoolean() == true) {
    extensions.findByName("gradleEnterprise")?.withGroovyBuilder {
        getProperty("buildScan").withGroovyBuilder {
            setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
            setProperty("termsOfServiceAgree", "yes")
        }
    }
}

includeBuild("build-logic")

include("gto-support-androidx-activity")
include("gto-support-androidx-annotation")
include("gto-support-androidx-collection")
include("gto-support-androidx-compose")
include("gto-support-androidx-compose-material3")
include("gto-support-androidx-constraintlayout")
include("gto-support-androidx-core")
include("gto-support-androidx-databinding")
include("gto-support-androidx-drawerlayout")
include("gto-support-androidx-fragment")
include("gto-support-androidx-lifecycle")
include("gto-support-androidx-recyclerview")
include("gto-support-androidx-room")
include("gto-support-androidx-test-junit")
include("gto-support-androidx-viewpager2")
include("gto-support-androidx-work")
include("gto-support-animation")
include("gto-support-api-base")
include("gto-support-api-okhttp3")
include("gto-support-appcompat")
include("gto-support-base")
include("gto-support-compat")
include("gto-support-core")
include("gto-support-dagger")
include("gto-support-db")
include("gto-support-db-coroutines")
include("gto-support-db-livedata")
include("gto-support-db-stream")
include("gto-support-eventbus")
include("gto-support-facebook")
include("gto-support-facebook-flipper")
include("gto-support-firebase-crashlytics")
include("gto-support-fluidsonic-locale")
include("gto-support-jsonapi")
include("gto-support-jsonapi-core")
include("gto-support-jsonapi-retrofit2")
include("gto-support-jsonapi-scarlet")
include("gto-support-kotlin-coroutines")
include("gto-support-leakcanary2")
include("gto-support-lottie")
include("gto-support-material-components")
include("gto-support-moshi")
include("gto-support-napier")
include("gto-support-okhttp3")
include("gto-support-okta")
include("gto-support-okta-oidc")
include("gto-support-picasso")
include("gto-support-play-auth")
include("gto-support-realm")
include("gto-support-recyclerview")
include("gto-support-recyclerview-advrecyclerview")
include("gto-support-retrofit2")
include("gto-support-scarlet")
include("gto-support-scarlet-actioncable")
include("gto-support-sync")
include("gto-support-util")
include("gto-support-viewpager")

include("testing:gto-support-dagger")
include("testing:gto-support-picasso")
include("testing:gto-support-timber")
