plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.kermit"

dependencies {
    api(libs.kermit)

    compileOnly(libs.timber)
}
