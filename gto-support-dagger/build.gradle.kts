plugins {
    id("gto-support.android-conventions")
    kotlin("kapt")
}

android.namespace = "org.ccci.gto.android.common.dagger"

dependencies {
    implementation(libs.dagger)

    // region EagerSingleton module
    compileOnly(libs.kotlin.coroutines)
    // endregion EagerSingleton module

    // region OkHttp3 Module
    compileOnly(libs.okhttp3)
    // endregion OkHttp3 Module

    // region Split Install module
    compileOnly(libs.dagger.hilt.android)
    compileOnly(libs.play.core)
    // endregion Split Install module

    kapt(libs.dagger.compiler)

    testImplementation(libs.dagger.hilt.android)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.play.core)
    kaptTest(libs.dagger.compiler)
}
