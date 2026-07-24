plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.room"
}

dependencies {
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
}
