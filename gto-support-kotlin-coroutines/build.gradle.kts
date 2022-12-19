plugins {
    id("gto-support.android-conventions")
}

dependencies {
    api(libs.kotlin.coroutines)

    // region LiveData extensions
    compileOnly(libs.androidx.lifecycle.livedata.core)
    // endregion LiveData extensions

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.lifecycle.livedata.core)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.turbine)
}
