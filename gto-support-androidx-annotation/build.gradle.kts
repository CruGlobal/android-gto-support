// Deprecated since v4.2.3, use the standard androidx-annotation library instead

plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    android {
        namespace = "org.ccci.gto.support.androidx.annotation"
    }

    configureIosTarget()
    configureJsTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.androidx.annotation)
            }
        }
    }
}
