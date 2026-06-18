plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    androidLibrary {
        namespace = "org.ccci.gto.android.common.jsonapi"
    }

    configureJvmTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":gto-support-jsonapi-core"))
            }
        }

        jvmMain {
            dependencies {
                api(libs.json)
            }
        }
    }
}
