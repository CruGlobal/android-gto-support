import com.android.build.api.dsl.androidLibrary

plugins {
    id("gto-support.multiplatform-conventions")
}

kotlin {
    configureAndroidLibraryTarget()
    configureJvmTarget()

    androidLibrary {
        namespace = "org.ccci.gto.android.common.jsonapi"
    }

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
