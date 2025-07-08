plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.support.androidx.test.junit"
}

kotlin {
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
