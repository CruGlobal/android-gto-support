plugins {
    id("gto-support.android-conventions")
}

dependencies {
    implementation(project(":gto-support-db"))

    api(libs.guava.listenablefuture)

    implementation(libs.androidx.concurrent.futures)
}
