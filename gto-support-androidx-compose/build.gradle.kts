plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.compose"
    configureCompose(project)
}

dependencies {
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui)

    // region Linkify support
    implementation(libs.androidx.core)
    // endregion Linkify support
}
