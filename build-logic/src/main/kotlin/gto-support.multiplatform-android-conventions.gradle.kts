plugins {
    id("gto-support.multiplatform-conventions")
    id("com.android.library")
}

kotlin {
    configureAndroidTarget(project)
}

koverReport {
    defaults {
        mergeWith("debug")
    }
}
