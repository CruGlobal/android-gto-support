plugins {
    id("gto-support.android-conventions")
}

@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
android.namespace = "org.ccci.gto.android.common.db.stream"

dependencies {
    api(project(":gto-support-db"))

    api(libs.lightweightStream)
}
