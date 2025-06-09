plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.jsonapi.retrofit2"

    defaultConfig {
        consumerProguardFile("src/jvmMain/resources/META-INF/proguard/jsonapi-retrofit2.pro")
    }
}

kotlin {
    configureJvmTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":gto-support-jsonapi"))

                api(libs.retrofit)
                implementation(libs.androidx.annotation)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.json)
                implementation(libs.jsonUnit.assertj)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.okhttp3.mockwebserver)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.json)
            }
        }
    }
}
