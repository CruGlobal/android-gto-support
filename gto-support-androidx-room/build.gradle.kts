plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.room"
    baseConfiguration(project)

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(libs.androidx.room.common)
}
