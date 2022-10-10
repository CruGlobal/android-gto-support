plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.compose"
    baseConfiguration(project)
    configureCompose(project)
}

dependencies {
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui)

    // region Linkify support
    implementation(libs.androidx.core)
    // endregion Linkify support
}
