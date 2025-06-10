plugins {
    id("gto-support.multiplatform-android-conventions")
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "org.ccci.gto.android.common.compose"
}

kotlin {
    configureIosTarget()
}
