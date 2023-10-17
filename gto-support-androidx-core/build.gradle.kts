plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.androidx.core"

dependencies {
    api(libs.androidx.core)

    implementation(libs.timber)

    testImplementation(kotlin("test"))

    // region Context.localize()
    compileOnly(project(":gto-support-util"))
    testImplementation(project(":gto-support-util"))
    // endregion Context.localize()
}
