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
        val androidMain by getting {
            dependencies {
                api(libs.androidx.annotation)
            }
        }
    }
}
