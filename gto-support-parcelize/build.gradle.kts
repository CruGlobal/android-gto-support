plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    androidLibrary {
        namespace = "org.ccci.gto.android.common.parcelize"
    }

    configureIosTarget()
    configureJsTarget()
}
