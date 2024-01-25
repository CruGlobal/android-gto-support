plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.compose.material3"
    configureCompose(project)
}

dependencies {
    api(libs.androidx.compose.material3)

    implementation(project(":gto-support-androidx-compose"))

    testImplementation(libs.kotlin.coroutines.test)

    // region Linkify support
    implementation(libs.androidx.core)
    // endregion Linkify support

    // region pagerTabIndicatorOffsetModifier
    compileOnly(libs.accompanist.pager)
    // endregion pagerTabIndicatorOffsetModifier

    // region PullRefreshIndicator
    compileOnly(libs.androidx.compose.material)
    // endregion PullRefreshIndicator

    // region LinearProgressIndicator bug
    testImplementation(libs.accompanist.pager)
    testImplementation(libs.androidx.compose.material)
    // endregion LinearProgressIndicator bug
}
