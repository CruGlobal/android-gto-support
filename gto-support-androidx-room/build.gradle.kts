plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.room"

    compileOptions.isCoreLibraryDesugaringEnabled = true
    testFixtures.enable = true
}

dependencies {
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    testFixturesApi(libs.androidx.room.runtime)
    testFixturesApi(libs.junit)
    testFixturesImplementation(libs.androidx.arch.core.runtime)
    testFixturesImplementation(libs.androidx.test)
    testFixturesCompileOnly(libs.kotlin.coroutines)
}
