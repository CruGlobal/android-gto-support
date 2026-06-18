plugins {
    id("gto-support.android-conventions")
    id("compose-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.compose"
    testFixtures.enable = true
}

dependencies {
    api(project(":gto-support-compose"))

    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui)

    // region Linkify support
    implementation(libs.androidx.core)
    // endregion Linkify support

    testFixturesImplementation(kotlin("test"))
    testFixturesImplementation(libs.androidx.compose.ui)
    testFixturesImplementation(libs.kotlin.coroutines)
}
