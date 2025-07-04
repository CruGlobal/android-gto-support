import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
// context(Project)
internal fun KotlinMultiplatformExtension.configureAndroidTarget(project: Project) {
    androidTarget {
        publishAllLibraryVariants()
    }

    project.extensions.configure<LibraryExtension> {
        baseConfiguration(project)
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
