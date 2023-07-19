plugins {
    id("gto-support.android-testing-conventions")
}

android.namespace = "org.ccci.gto.android.common.testing.timber"

dependencies {
    api(libs.timber)
}
