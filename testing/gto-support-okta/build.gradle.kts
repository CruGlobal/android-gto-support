// @Deprecated("since v3.11.2, use testFixtures(gto-support-okta) instead")
plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidTestingLibrary()

dependencies {
    api(testFixtures(project(":gto-support-okta")))
    api(libs.okta)
}
