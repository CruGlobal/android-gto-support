plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.androidx.drawerlayout"

dependencies {
    implementation(project(":gto-support-util"))

    api(libs.androidx.drawerlayout)
}
