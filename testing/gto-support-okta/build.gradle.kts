// @Deprecated("since v3.11.2, use testFixtures(gto-support-okta-oidc) instead")
plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidTestingLibrary()

dependencies {
    api(testFixtures(project(":gto-support-okta-oidc")))
}
