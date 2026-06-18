plugins {
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover")
    id("ktlint-conventions")
    id("publishing-conventions")
}

android {
    compileSdk = versionCatalog.findVersion("android-sdk-compile").get().requiredVersion.toInt()

    defaultConfig {
        minSdk = versionCatalog.findVersion("android-sdk-min").get().requiredVersion.toInt()

        consumerProguardFiles(rootProject.file("proguard-consumer-jetbrains.pro"))
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true

            all {
                // increase unit tests max heap size
                it.maxHeapSize = "2g"
            }
        }
    }
}

// not all projects actually have tests
tasks.withType<Test> {
    failOnNoDiscoveredTests.set(false)
}

kotlin {
    configureJvmToolchain(project)
}

dependencies {
    compileOnly(versionCatalog.findLibrary("androidx-annotation").get())

    testImplementation(versionCatalog.findBundle("android-test-framework").get())
}

baseDependencyResolutionStrategy()
configureTestSharding()
