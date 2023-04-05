plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.facebook"
}

dependencies {
    // region facebook-login
    compileOnly(libs.facebook.login)
    testImplementation(libs.facebook.login)
    // endregion facebook-login

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.turbine)
}
