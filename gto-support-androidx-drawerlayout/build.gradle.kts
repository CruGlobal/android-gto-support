plugins {
    id("gto-support.android-conventions")
}

dependencies {
    implementation(project(":gto-support-util"))

    api(libs.androidx.drawerlayout)
}
