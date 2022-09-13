plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.kotlin.serialization)
}

configureAndroidLibrary()

android {
    namespace = "org.ccci.gto.android.common.okta"

    testFixtures.enable = true
    defaultConfig.consumerProguardFiles("src/main/proguard-consumer.pro")
}

dependencies {
    api(libs.okta.auth.foundation)

    implementation(project(":gto-support-base"))
    implementation(project(":gto-support-util"))

    implementation(libs.androidx.security.crypto)

    // region CredentialBootstrap
    compileOnly(libs.okta.auth.foundation.bootstrap)
    // endregion CredentialBootstrap

    // region DataStoreTokenStorage
    compileOnly(libs.androidx.datastore)
    testImplementation(libs.androidx.datastore)
    // endregion DataStoreTokenStorage

    // import legacy okta-oidc module. this transitive dependency will be removed over the next several releases of gto-support
    // v3.14.0 - api dependency
    // v3.15.0 - implementation dependency
    // v3.16.0 - removed
    api(project(":gto-support-okta-oidc"))
    testFixturesApi(testFixtures(project(":gto-support-okta-oidc")))

    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.turbine)
}
