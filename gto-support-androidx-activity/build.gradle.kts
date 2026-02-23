plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.activity"

    configureCompose(project)
}

dependencies {
    api(libs.androidx.activity)

    // region Compose Extensions
    compileOnly(libs.androidx.activity.compose)
    // endregion Compose Extensions
}
