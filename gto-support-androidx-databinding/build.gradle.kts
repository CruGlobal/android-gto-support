android {
    buildFeatures.dataBinding = true
}

dependencies {
    implementation(project(":gto-support-util"))

    compileOnly(libs.androidx.core)
}
