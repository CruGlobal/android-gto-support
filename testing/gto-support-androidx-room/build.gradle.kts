plugins {
    id("gto-support.android-testing-conventions")
}

android.namespace = "org.ccci.gto.android.common.testing.androidx.room"

dependencies {
    api(libs.androidx.room.runtime)
    api(libs.junit)

    implementation(libs.androidx.test)
}
