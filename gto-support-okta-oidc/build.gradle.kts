plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.okta.oidc"
    baseConfiguration(project)

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

    // region LiveData
    compileOnly(libs.androidx.lifecycle.livedata.ktx)
    // endregion LiveData

    // region OkHttpOktaHttpClient
    compileOnly(libs.okhttp3)
    // endregion OkHttpOktaHttpClient

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.lifecycle.livedata.core)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.bundles.okhttp3.mockwebserver)
}
