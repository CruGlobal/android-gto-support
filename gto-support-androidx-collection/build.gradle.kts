plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.parcelize")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.collection"
    baseConfiguration(project)
}

dependencies {
    api(libs.androidx.collection)

    testImplementation(libs.androidx.collection.ktx)
}
