plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.room"
    baseConfiguration(project)

    compileOptions.isCoreLibraryDesugaringEnabled = true
    testFixtures.enable = true
}

dependencies {
    implementation(libs.androidx.room.common)

    testFixturesApi(libs.androidx.room.runtime)
    testFixturesApi(libs.junit)
    testFixturesImplementation(libs.androidx.arch.core.runtime)
    testFixturesImplementation(libs.androidx.test)
    testFixturesCompileOnly(libs.kotlin.coroutines)
}
