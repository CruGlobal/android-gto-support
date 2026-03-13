plugins {
    id("gto-support.multiplatform-android-conventions")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "org.ccci.gto.android.common.sync"
}

kotlin {
    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kermit)
                implementation(libs.kotlin.coroutines)

                // region Composables
                compileOnly(libs.compose.runtime)
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
            }
        }

        commonTest {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.turbine)
            }
        }
    }
}
