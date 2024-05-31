plugins {
    id("gto-support.android-conventions")
    alias(libs.plugins.ksp)
}

android.namespace = "org.ccci.gto.android.common.dagger"

dependencies {
    implementation(libs.dagger)

    // region EagerSingleton module
    compileOnly(libs.kotlin.coroutines)
    // endregion EagerSingleton module

    // region jsonapi Module
    compileOnly(project(":gto-support-jsonapi"))
    // endregion jsonapi Module

    // region OkHttp3 Module
    compileOnly(libs.okhttp3)
    // endregion OkHttp3 Module

    // region Split Install module
    compileOnly(libs.dagger.hilt.android)
    compileOnly(libs.play.featuredelivery)
    testImplementation(libs.play.featuredelivery)
    // endregion Split Install module

    ksp(libs.dagger.compiler)

    testImplementation(libs.dagger.hilt.android)
    testImplementation(libs.kotlin.coroutines.test)
    kspTest(libs.dagger.compiler)
}
