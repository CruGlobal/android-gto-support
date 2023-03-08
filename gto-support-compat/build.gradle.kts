plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.compat"

dependencies {
    implementation(libs.weakdelegate)
}
