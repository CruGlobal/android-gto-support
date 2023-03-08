plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.eventbus"

dependencies {
    implementation(project(":gto-support-androidx-lifecycle"))
    implementation(project(":gto-support-core"))

    api(libs.eventbus)

    // region TimberLogger
    compileOnly(libs.timber)
    // endregion TimberLogger

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.lifecycle.runtime.testing)
    testImplementation(libs.kotlin.coroutines.test)
}
