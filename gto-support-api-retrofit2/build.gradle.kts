// @Deprecated("since v3.10.0, use gto-support-retrofit2 module instead")
plugins {
    id("com.android.library")
}

configureAndroidLibrary()

dependencies {
    api(project(":gto-support-retrofit2"))
}
