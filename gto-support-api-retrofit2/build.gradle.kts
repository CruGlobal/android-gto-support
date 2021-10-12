plugins {
    id("com.android.library")
}

configureAndroidLibrary()

dependencies {
    api(project(":gto-support-retrofit2"))
}
