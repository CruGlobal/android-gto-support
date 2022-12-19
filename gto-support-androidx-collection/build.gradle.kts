plugins {
    id("gto-support.android-conventions")
    kotlin("plugin.parcelize")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.collection"
}

dependencies {
    api(libs.androidx.collection)

    testImplementation(libs.androidx.collection.ktx)
}
