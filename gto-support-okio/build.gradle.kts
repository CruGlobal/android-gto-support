plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    android {
        namespace = "org.ccci.gto.android.common.okio"
    }

    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.okio)
            }
        }
    }
}
