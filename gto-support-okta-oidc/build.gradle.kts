plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.okta.oidc"

    testFixtures.enable = true
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(libs.okta)

    implementation(project(":gto-support-base"))
    implementation(project(":gto-support-compat"))
    implementation(project(":gto-support-util"))

    implementation(libs.splitties.bitflags)
    implementation(libs.timber)

    // region Coroutines
    compileOnly(libs.kotlin.coroutines)
    // endregion Coroutines

    // region OkHttpOktaHttpClient
    compileOnly(libs.okhttp3)
    // endregion OkHttpOktaHttpClient

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.bundles.okhttp3.mockwebserver)
    testImplementation(libs.turbine)
}
