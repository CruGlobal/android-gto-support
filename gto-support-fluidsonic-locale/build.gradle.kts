plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.ccci.gto.android.common.fluidsonic.locale"
    baseConfiguration(project)
}

kotlin {
    baseConfiguration()
    configureTargets(project)

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.fluidsonic.locale)
            }
        }
    }
}
