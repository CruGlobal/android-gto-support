plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.okio"
}

kotlin {
    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.okio)
            }
        }
    }
}
