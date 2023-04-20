import com.android.build.gradle.LibraryExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

fun KotlinMultiplatformExtension.baseConfiguration() {
    (this as ExtensionAware).extensions.configure<NamedDomainObjectContainer<KotlinSourceSet>>("sourceSets") {
        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
// context(Project)
fun KotlinMultiplatformExtension.configureTargets(project: Project) {
    configureAndroidTargets(project)
    configureIosTargets()
    configureJsTargets()
}

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
// context(Project)
fun KotlinMultiplatformExtension.configureAndroidTargets(project: Project) {
    android {
        publishAllLibraryVariants()
    }

    project.extensions.configure<LibraryExtension> {
        sourceSets {
            named("main") { setRoot("src/androidMain") }
            named("androidTest") { setRoot("src/androidAndroidTest") }
            named("test") {
                setRoot("src/androidTest")
                resources.srcDir("src/commonTest/resources")
            }
        }
    }
}

fun KotlinMultiplatformExtension.configureIosTargets() {
    ios {
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
}

fun KotlinMultiplatformExtension.configureJsTargets() {
    js {
        binaries.library()
        browser {
            testTask { useMocha() }
        }
        nodejs()
    }
}
