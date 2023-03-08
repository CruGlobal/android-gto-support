plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.db.stream"

dependencies {
    api(project(":gto-support-db"))

    api(libs.lightweightStream)
}
