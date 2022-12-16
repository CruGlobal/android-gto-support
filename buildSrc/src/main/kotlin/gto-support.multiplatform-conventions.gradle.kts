plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    baseConfiguration(project)
}

kotlin {
    baseConfiguration()
    configureTargets(project)
}
