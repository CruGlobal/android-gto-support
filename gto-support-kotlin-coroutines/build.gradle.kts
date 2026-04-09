plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.kotlin.coroutines"
}

kotlin {
    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlin.coroutines)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.turbine)
            }
        }

        androidMain {
            dependencies {
                // region ConnectivityManager extensions
                compileOnly(libs.androidx.core.ktx)
                // endregion ConnectivityManager extensions

                // region LiveData extensions
                compileOnly(libs.androidx.lifecycle.livedata.core)
                // endregion LiveData extensions
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.androidx.lifecycle.livedata.core)
            }
        }
    }
}
