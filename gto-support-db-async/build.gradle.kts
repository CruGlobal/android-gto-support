plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(project(":gto-support-db"))

    api(libs.guava.listenablefuture)

    implementation(libs.androidx.concurrent.futures)
}
