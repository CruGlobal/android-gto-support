plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.work"
}

dependencies {
    api(libs.androidx.work.runtime)

    compileOnly(libs.timber)
}
