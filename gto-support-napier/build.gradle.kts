plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.napier"

dependencies {
    api(libs.napier)

    compileOnly(libs.timber)
}
