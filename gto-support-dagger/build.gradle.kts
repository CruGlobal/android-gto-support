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

    // region ViewModel Module
    compileOnly(libs.androidx.lifecycle.viewmodel)
    compileOnly(libs.androidx.lifecycle.viewmodel.savedstate)
    // endregion ViewModel Module

    // region WorkManager Module
    compileOnly(libs.androidx.work.runtime)
    // endregion WorkManager Module

    kapt(libs.dagger.compiler)

    testImplementation(libs.androidx.lifecycle.viewmodel)
    testImplementation(libs.dagger.hilt.android)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.play.core)
    kaptTest(libs.dagger.compiler)
}
