plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.db.coroutines"

dependencies {
    api(project(":gto-support-db"))

    api(libs.kotlin.coroutines)

    testImplementation(libs.kotlin.coroutines.test)
}
