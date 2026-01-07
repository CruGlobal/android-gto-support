plugins {
    id("gto-support.android-conventions")
    alias(libs.plugins.kotlin.serialization)
}

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

    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.turbine)
}
