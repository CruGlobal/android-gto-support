plugins {
    id("gto-support.android-conventions")
}

dependencies {
    api(project(":gto-support-db"))

    implementation(libs.androidx.collection)
    api(libs.androidx.lifecycle.livedata)

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.kotlin.coroutines)
}
