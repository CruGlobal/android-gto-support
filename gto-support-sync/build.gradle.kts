plugins {
    id("gto-support.multiplatform-android-conventions")
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

        commonTest {
            dependencies {
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.turbine)
            }
        }
    }
}
