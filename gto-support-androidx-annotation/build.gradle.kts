// Deprecated since v4.2.3, use the standard androidx-annotation library instead

plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.support.androidx.annotation"
}

kotlin {
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
