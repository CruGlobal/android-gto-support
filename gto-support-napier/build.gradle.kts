plugins {
    id("gto-support.android-conventions")
}

@Deprecated("Since v4.2.0, Kotlin Multiplatform has migrated to using Kermit for logging")

android.namespace = "org.ccci.gto.android.common.napier"

dependencies {
    api(libs.napier)

    compileOnly(libs.timber)
}
