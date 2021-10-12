plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

configureAndroidLibrary()

dependencies {
    implementation(project(":gto-support-moshi"))
    implementation(project(":gto-support-scarlet"))

    api(libs.scarlet.core)
    implementation(libs.scarlet.messageadapter.builtin)
    implementation(libs.scarlet.messageadapter.moshi) {
        // HACK: there is no need to require the reflection based moshi-kotlin adapter
        exclude(group = "com.squareup.moshi", module = "moshi-kotlin")
    }
    compileOnly(libs.scarlet.websocket.okhttp)

    testImplementation(libs.json)
    testImplementation(libs.jsonUnit)
    testImplementation(libs.jsonUnit.fluent)
    testImplementation(libs.junitParams)

    kapt(libs.moshi.kotlin.codegen)
}
