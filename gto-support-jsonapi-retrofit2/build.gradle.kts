import com.android.build.api.dsl.androidLibrary

plugins {
    id("gto-support.multiplatform-conventions")
}

kotlin {
    configureAndroidLibraryTarget()
    configureJvmTarget()

    androidLibrary {
        namespace = "org.ccci.gto.android.common.jsonapi.retrofit2"

        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.file("src/jvmMain/resources/META-INF/proguard/jsonapi-retrofit2.pro")
        }
    }

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
