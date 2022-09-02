plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

android {
    namespace = "org.ccci.gto.android.common.okta"

    testFixtures.enable = true
}

dependencies {
    api(libs.okta.auth.foundation)

    // import legacy okta-oidc module. this transitive dependency will be removed over the next several releases of gto-support
    // v3.14.0 - api dependency
    // v3.15.0 - implementation dependency
    // v3.16.0 - removed
    api(project(":gto-support-okta-oidc"))
    testFixturesApi(testFixtures(project(":gto-support-okta-oidc")))

    testImplementation(libs.kotlin.coroutines.test)
}
