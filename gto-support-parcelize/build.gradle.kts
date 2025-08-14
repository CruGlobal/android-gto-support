plugins {
    id("gto-support.multiplatform-android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.parcelize"
}

kotlin {
    configureIosTarget()
}
