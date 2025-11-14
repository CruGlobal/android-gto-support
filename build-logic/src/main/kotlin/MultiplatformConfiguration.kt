import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

@Suppress("UnstableApiUsage")
fun KotlinMultiplatformExtension.configureAndroidLibraryTarget() {
    project.pluginManager.apply("com.android.kotlin.multiplatform.library")

    androidLibrary {
        compileSdk = project.libs.findVersion("android-sdk-compile").get().requiredVersion.toInt()
        minSdk = project.libs.findVersion("android-sdk-min").get().requiredVersion.toInt()
    }
}

fun KotlinMultiplatformExtension.configureIosTarget() {
    iosArm64 { enableBackgroundTests() }
    iosX64 { enableBackgroundTests() }
    iosSimulatorArm64 { enableBackgroundTests() }
}

fun KotlinMultiplatformExtension.configureJsTarget() {
    js {
        binaries.library()
        browser {
            testTask { useMocha() }
        }
        nodejs()
    }
}

fun KotlinMultiplatformExtension.configureJvmTarget() {
    jvm()
}

private fun KotlinNativeTarget.enableBackgroundTests() {
    // enable running ios tests on a background thread as well
    // configuration copied from: https://github.com/square/okio/pull/929
    if (this is KotlinNativeTargetWithTests<*>) {
        binaries {
            // Configure a separate test where code runs in background
            test("background", setOf(DEBUG)) {
                freeCompilerArgs += "-trw"
            }
        }
        testRuns.create("background") {
            setExecutionSourceFrom(binaries.getByName("backgroundDebugTest") as TestExecutable)
        }
    }
}
