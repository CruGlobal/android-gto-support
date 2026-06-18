plugins {
    id("gto-support.multiplatform-android-conventions")
    id("compose-multiplatform-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.lifecycle"
}

kotlin {
    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.androidx.lifecycle.common)
                implementation(libs.androidx.lifecycle.runtime)

                // region Composables
                compileOnly(libs.compose.runtime)
                // endregion Composables
            }
        }

        commonTest {
            dependencies {
                implementation(libs.androidx.lifecycle.runtime.testing)
                implementation(libs.kotlin.coroutines.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.lifecycle.livedata)
                implementation(libs.androidx.lifecycle.viewmodel)

                implementation(libs.weakdelegate)

                // region Composables
                compileOnly(libs.androidx.lifecycle.runtime.compose)
                // endregion Composables

                // region ObservableLiveData
                compileOnly(libs.androidx.databinding.runtime)
                // endregion ObservableLiveData

                // region SavedStateHandle
                compileOnly(libs.androidx.lifecycle.viewmodel.savedstate)
                // endregion SavedStateHandle
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.lifecycle.viewmodel.savedstate)
            }
        }

        nativeMain {
            dependencies {
                // HACK: compileOnly dependencies aren't supported on Kotlin/Native, so promote them to implementation
                implementation(libs.compose.runtime)
            }
        }
    }
}
