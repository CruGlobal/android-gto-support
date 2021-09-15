plugins {
    id("org.jetbrains.kotlin.plugin.parcelize")
}

dependencies {
    api(libs.androidx.collection)

    testImplementation(libs.androidx.collection.ktx)
}
