plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.compose.material3"
    baseConfiguration(project)
    configureCompose(project)
}

dependencies {
    api(libs.androidx.compose.material3)

    implementation(project(":gto-support-androidx-compose"))

    // region Linkify support
    implementation(libs.androidx.core)
    // endregion Linkify support

    // region pagerTabIndicatorOffsetModifier
    compileOnly(libs.accompanist.pager)
    // endregion pagerTabIndicatorOffsetModifier
}
