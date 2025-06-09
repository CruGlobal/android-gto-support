import com.android.build.api.dsl.androidLibrary

// Deprecated since v4.2.3, use the standard androidx-annotation library instead

plugins {
    id("gto-support.multiplatform-conventions")
}

kotlin {
    configureAndroidLibraryTarget()
    configureIosTarget()
    configureJsTarget()

    androidLibrary {
        namespace = "org.ccci.gto.support.androidx.annotation"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.androidx.annotation)
            }
        }
    }
}
