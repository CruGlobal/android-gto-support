import com.android.build.api.dsl.androidLibrary

plugins {
    id("gto-support.multiplatform-conventions")
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    configureAndroidLibraryTarget()
    configureIosTarget()

    androidLibrary {
        namespace = "org.ccci.gto.android.common.compose"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.ui)
            }
        }
    }
}
