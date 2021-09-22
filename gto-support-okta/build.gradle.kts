android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(libs.okta)

    implementation(project(":gto-support-base"))
    implementation(project(":gto-support-util"))

    // region Coroutines
    compileOnly(libs.kotlin.coroutines)
    // endregion Coroutines

    // region LiveData
    compileOnly(libs.androidx.lifecycle.livedata.ktx)
    // endregion LiveData

    // region OkHttpOktaHttpClient
    compileOnly(libs.okhttp3)
    // endregion OkHttpOktaHttpClient

    testImplementation(project(":testing:gto-support-okta"))
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.okhttp3.mockwebserver)
}