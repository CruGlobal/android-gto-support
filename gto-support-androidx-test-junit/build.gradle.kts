import com.android.build.api.dsl.androidLibrary

plugins {
    id("gto-support.multiplatform-conventions")
}

kotlin {
    configureAndroidLibraryTarget()
    configureIosTarget()
    configureJsTarget()
    configureJvmTarget()

    androidLibrary {
        namespace = "org.ccci.gto.support.androidx.test.junit"
    }

    sourceSets {
        androidMain {
            dependencies {
                api(libs.androidx.test.junit)
            }
        }
    }
}
