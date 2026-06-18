plugins {
    id("gto-support.multiplatform-android-conventions")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidLibrary {
        namespace = "org.ccci.gto.android.common.compose"
    }

    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.foundation)
                implementation(libs.compose.ui)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.gtoSupportAndroidxTestJunit)

                implementation(libs.compose.ui.test)
                implementation(libs.turbine)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.compose.ui.test.manifest)
            }
        }

        androidHostTest {
            dependencies {
                implementation(libs.robolectric)
            }
        }
    }
}
