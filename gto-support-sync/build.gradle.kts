plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(project(":gto-support-core"))
    implementation(project(":gto-support-androidx-collection"))
    implementation(project(":gto-support-compat"))

    compileOnly(libs.androidx.swiperefreshlayout)
}
