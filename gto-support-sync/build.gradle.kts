plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    implementation(project(":gto-support-androidx-collection"))
    api(project(":gto-support-core"))

    compileOnly(libs.androidx.swiperefreshlayout)
}
