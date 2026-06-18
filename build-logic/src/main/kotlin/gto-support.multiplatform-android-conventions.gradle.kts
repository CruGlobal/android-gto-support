plugins {
    id("gto-support.multiplatform-conventions")
    id("com.android.library")
}

android {
    baseConfiguration(project)
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }

    sourceSets {
        androidUnitTest {
            dependencies {
                implementation(versionCatalog.findBundle("android-test-framework").get())
            }
        }
    }
}

configureTestSharding()
