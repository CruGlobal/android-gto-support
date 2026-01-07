plugins {
    id("gto-support.android-conventions")
    alias(libs.plugins.moshiX)
}

android {
    namespace = "org.ccci.gto.android.common.scarlet.actioncable"
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

moshi {
    applyMoshiDependency.set(false)
}

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
    testImplementation(libs.jsonUnit.assertj)
    testImplementation(libs.junitParams)
}
