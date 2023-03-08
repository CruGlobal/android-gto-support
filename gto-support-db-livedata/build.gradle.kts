plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.db.livedata"

dependencies {
    api(project(":gto-support-db"))
    implementation(project(":gto-support-androidx-lifecycle"))

    implementation(libs.androidx.collection)
    api(libs.androidx.lifecycle.livedata)

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.kotlin.coroutines)
}
