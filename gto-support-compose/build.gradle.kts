plugins {
    id("gto-support.multiplatform-android-conventions")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "org.ccci.gto.android.common.compose"
}

kotlin {
    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.foundation)
                implementation(libs.compose.ui)
            }
        }
    }
}
