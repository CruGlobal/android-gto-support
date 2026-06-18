plugins {
    id("gto-support.multiplatform-android-conventions")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    buildFeatures.compose = true
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // the runtime dependency is required to build a library when compose is enabled
                implementation(versionCatalog.findLibrary("compose-runtime").get())
            }
        }
    }
}
