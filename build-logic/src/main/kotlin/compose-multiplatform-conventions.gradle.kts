plugins {
    id("gto-support.multiplatform-conventions")
    id("org.jetbrains.kotlin.plugin.compose")
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
