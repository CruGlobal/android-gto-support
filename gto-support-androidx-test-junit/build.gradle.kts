plugins {
    id("gto-support.multiplatform-conventions")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.ccci.gto.support.androidx.test.junit"
}

kotlin {
    sourceSets {
        val androidMain by getting {
            dependencies {
                api(libs.androidx.test.junit)
            }
        }
    }
}
