plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.jsonapi"
}

kotlin {
    configureJvmTarget()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":gto-support-jsonapi-core"))
            }
        }

        val jvmMain by getting {
            dependencies {
                api(libs.json)
            }
        }
    }
}
