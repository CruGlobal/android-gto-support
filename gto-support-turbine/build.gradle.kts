plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    android {
        namespace = "org.ccci.gto.support.turbine"
    }

    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.turbine)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}
