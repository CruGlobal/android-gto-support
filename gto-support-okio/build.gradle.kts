import com.android.build.api.dsl.androidLibrary

plugins {
    id("gto-support.multiplatform-conventions")
}

kotlin {
    configureAndroidLibraryTarget()
    configureIosTarget()

    androidLibrary {
        namespace = "org.ccci.gto.android.common.okio"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.okio)
            }
        }
    }
}
