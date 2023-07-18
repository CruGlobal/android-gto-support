plugins {
    id("gto-support.multiplatform-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.fluidsonic.locale"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.fluidsonic.locale)
            }
        }
    }
}
