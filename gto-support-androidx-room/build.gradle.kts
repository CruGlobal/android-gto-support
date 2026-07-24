plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    android {
        namespace = "org.ccci.gto.android.common.androidx.room"
    }

    configureIosTarget()

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.room.common)
                implementation(libs.androidx.room.runtime)
            }
        }
    }
}
