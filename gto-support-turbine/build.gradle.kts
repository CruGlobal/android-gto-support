plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.support.turbine"
}

kotlin {
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
