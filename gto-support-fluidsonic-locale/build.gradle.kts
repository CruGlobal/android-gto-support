plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    androidLibrary {
        namespace = "org.ccci.gto.android.common.fluidsonic.locale"
    }

    configureIosTarget()
    configureJsTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.fluidsonic.locale)
            }
        }
    }
}
