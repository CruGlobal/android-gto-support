plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()
android {
    configureCompose(project)
}
