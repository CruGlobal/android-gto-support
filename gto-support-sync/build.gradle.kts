plugins {
    id("gto-support.multiplatform-android-conventions")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    android {
        namespace = "org.ccci.gto.android.common.sync"
    }

    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.androidx.annotation)
                implementation(libs.kermit)
                implementation(libs.kotlin.coroutines)

                // region Composables
                compileOnly(libs.compose.runtime)
                compileOnly(libs.circuit.runtime)
                // endregion Composables
            }
        }

        androidMain {
            dependencies {
                api(project(":gto-support-core"))
                implementation(project(":gto-support-androidx-collection"))
                implementation(project(":gto-support-compat"))

                compileOnly(libs.androidx.swiperefreshlayout)
            }
        }

        nativeMain {
            dependencies {
                // HACK: compileOnly dependencies aren't supported on Kotlin/Native, so promote them to implementation
                implementation(libs.compose.runtime)
                implementation(libs.circuit.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.gtoSupportAndroidxTestJunit)

                implementation(libs.circuit.runtime)
                implementation(libs.compose.runtime)
                implementation(libs.compose.ui.test)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.turbine)
            }
        }

        androidHostTest {
            dependencies {
                implementation(libs.androidx.compose.ui.test.manifest)
                implementation(libs.robolectric)
            }
        }
    }
}
