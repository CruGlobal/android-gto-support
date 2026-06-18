plugins {
    id("gto-support.multiplatform-conventions")
    id("com.android.library")
}

kotlin {
    configureAndroidTarget(project)

    sourceSets {
        androidUnitTest {
            dependencies {
                implementation(versionCatalog.findBundle("android-test-framework").get())
            }
        }
    }
}

configureTestSharding()
