plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(project(":gto-support-androidx-lifecycle"))
    implementation(project(":gto-support-core"))
    compileOnly(project(":gto-support-db"))

    api(libs.eventbus)

    // region TimberLogger
    compileOnly(libs.timber)
    // endregion TimberLogger

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.lifecycle.runtime.testing)
    testImplementation(libs.kotlin.coroutines.test)
}
