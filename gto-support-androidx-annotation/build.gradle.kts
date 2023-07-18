plugins {
    id("gto-support.multiplatform-conventions")
}

android {
    namespace = "org.ccci.gto.support.androidx.annotation"
}

kotlin {
    sourceSets {
        val androidMain by getting {
            dependencies {
                api(libs.androidx.annotation)
            }
        }
    }
}
