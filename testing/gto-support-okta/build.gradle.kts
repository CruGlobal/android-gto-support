// @Deprecated("since v3.11.2, use testFixtures(gto-support-okta-oidc) instead")
plugins {
    id("gto-support.android-testing-conventions")
}

dependencies {
    api(testFixtures(project(":gto-support-okta-oidc")))
}
