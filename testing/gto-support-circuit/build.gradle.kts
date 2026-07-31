plugins {
    id("gto-support.multiplatform-android-testing-conventions")
    id("compose-multiplatform-conventions")
}

kotlin {
    android {
        namespace = "org.ccci.gto.android.common.testing.circuit"
    }

    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.circuit.overlay)
                implementation(libs.kotlin.coroutines)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}
