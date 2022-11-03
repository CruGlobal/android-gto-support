plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.ccci.gto.support.androidx.annotation"
    baseConfiguration(project)
}

kotlin {
    baseConfiguration()
    configureTargets(project)

    sourceSets {
        val androidMain by getting {
            dependencies {
                api(libs.androidx.annotation)
            }
        }
    }
}
