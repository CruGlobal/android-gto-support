plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()
android {
    configureCompose(project)
}

dependencies {
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui)
}
