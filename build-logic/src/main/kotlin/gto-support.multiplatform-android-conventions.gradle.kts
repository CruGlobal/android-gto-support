plugins {
    id("gto-support.multiplatform-conventions")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    android {
        compileSdk = project.versionCatalog.findVersion("android-sdk-compile").get().requiredVersion.toInt()
        minSdk = project.versionCatalog.findVersion("android-sdk-min").get().requiredVersion.toInt()

        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.file(rootProject.file("proguard-consumer-jetbrains.pro"))
        }

        withHostTest {
            isIncludeAndroidResources = true
        }
        tasks.withType<Test> {
            failOnNoDiscoveredTests.set(false)
        }
    }

    sourceSets {
        androidHostTest {
            dependencies {
                implementation(versionCatalog.findBundle("android-test-framework").get())
            }
        }
    }
}

configureTestSharding()
