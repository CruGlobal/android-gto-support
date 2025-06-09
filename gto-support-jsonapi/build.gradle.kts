plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.jsonapi"
}

kotlin {
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
