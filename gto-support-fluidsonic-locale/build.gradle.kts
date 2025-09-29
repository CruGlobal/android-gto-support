import com.android.build.api.dsl.androidLibrary

plugins {
    id("gto-support.multiplatform-conventions")
}

kotlin {
    configureAndroidLibraryTarget()
    configureIosTarget()
    configureJsTarget()

    androidLibrary {
        namespace = "org.ccci.gto.android.common.fluidsonic.locale"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.fluidsonic.locale)
            }
        }
    }
}
