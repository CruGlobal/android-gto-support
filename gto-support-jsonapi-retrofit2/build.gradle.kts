plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.jsonapi.retrofit2"
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
