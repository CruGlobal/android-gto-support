plugins {
    id("gto-support.android-conventions")
}

@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
android.namespace = "org.ccci.gto.android.common.db.livedata"

dependencies {
    api(project(":gto-support-db"))
    implementation(project(":gto-support-androidx-lifecycle"))

    implementation(libs.androidx.collection)
    api(libs.androidx.lifecycle.livedata)

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.kotlin.coroutines)
}
