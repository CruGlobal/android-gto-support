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
        val commonMain by getting {
            dependencies {
                api(project(":gto-support-jsonapi"))

                api(libs.retrofit)
                implementation(libs.androidx.annotation)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.json)
                implementation(libs.jsonUnit)
                implementation(libs.jsonUnit.fluent)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.okhttp3.mockwebserver)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.json)
            }
        }
    }
}
