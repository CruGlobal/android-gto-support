plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    android {
        namespace = "org.ccci.gto.android.common.androidx.room"
    }

    configureIosTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.androidx.room.common)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.room.runtime)
            }
        }
    }
}
