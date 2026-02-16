plugins {
    id("gto-support.multiplatform-conventions")
    id("com.android.library")
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }
}

android {
    baseConfiguration(project)
}
