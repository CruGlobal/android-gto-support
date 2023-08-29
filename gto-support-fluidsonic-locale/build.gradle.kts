plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.fluidsonic.locale"
}

kotlin {
    configureIosTarget()
    configureJsTarget()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.fluidsonic.locale)
            }
        }
    }
}
