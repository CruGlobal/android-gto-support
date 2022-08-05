plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()
android {
    configureCompose(project)
}

dependencies {
    api(libs.androidx.compose.material3)

    implementation(project(":gto-support-androidx-compose"))

    // region Linkify support
    implementation(libs.androidx.core)
    // endregion Linkify support
}
