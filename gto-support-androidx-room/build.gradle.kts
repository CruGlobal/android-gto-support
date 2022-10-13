plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.room"
    baseConfiguration(project)
}

dependencies {
    implementation(libs.androidx.room.common)
}
