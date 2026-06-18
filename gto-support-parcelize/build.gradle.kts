plugins {
    id("gto-support.multiplatform-android-conventions")
}

kotlin {
    android {
        namespace = "org.ccci.gto.android.common.parcelize"
    }

    configureIosTarget()
    configureJsTarget()
}
