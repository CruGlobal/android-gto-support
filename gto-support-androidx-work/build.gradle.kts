plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.work"
    baseConfiguration(project)
}

dependencies {
    api(libs.androidx.work.runtime)

    compileOnly(libs.timber)
}
