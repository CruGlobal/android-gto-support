plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    androidLibrary {
        namespace = "org.ccci.gto.support.androidx.test.junit"
    }

    configureIosTarget()
    configureJsTarget()
    configureJvmTarget()

    sourceSets {
        val androidMain by getting {
            dependencies {
                api(libs.androidx.test.junit)
            }
        }
    }
}
