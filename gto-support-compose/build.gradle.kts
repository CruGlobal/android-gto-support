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

        androidUnitTest {
            dependencies {
                implementation(libs.robolectric)
            }
        }
    }
}
