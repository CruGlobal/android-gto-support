plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.kotlin.coroutines"

dependencies {
    api(libs.kotlin.coroutines)

    // region ConnectivityManager extensions
    compileOnly(libs.androidx.core.ktx)
    testImplementation(libs.androidx.core.ktx)
    // endregion ConnectivityManager extensions

    // region LiveData extensions
    compileOnly(libs.androidx.lifecycle.livedata.core)
    // endregion LiveData extensions

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.lifecycle.livedata.core)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.turbine)
}
