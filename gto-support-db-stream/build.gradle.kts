plugins {
    id("gto-support.android-conventions")
}

dependencies {
    api(project(":gto-support-db"))

    api(libs.lightweightStream)
}
