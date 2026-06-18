plugins {
    id("gto-support.android-conventions")
    id("compose-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.activity"
}

dependencies {
    api(libs.androidx.activity)

    // region Compose Extensions
    compileOnly(libs.androidx.activity.compose)
    // endregion Compose Extensions
}
