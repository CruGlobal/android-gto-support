plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.play.auth"

dependencies {
    api(libs.play.auth)

    implementation(project(":gto-support-kotlin-coroutines"))
}
