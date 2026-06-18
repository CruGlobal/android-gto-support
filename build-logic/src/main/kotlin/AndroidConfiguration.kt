import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
// context(Project)
internal fun LibraryExtension.baseConfiguration(project: Project) {
    configureSdk(project)
    configureProguardRules(project)
    configureTestOptions(project)
    project.configureDependencyResolutionStrategy()
}

private fun BaseExtension.configureSdk(project: Project) {
    compileSdkVersion(project.versionCatalog.findVersion("android-sdk-compile").get().requiredVersion.toInt())

    defaultConfig {
        minSdk = project.versionCatalog.findVersion("android-sdk-min").get().requiredVersion.toInt()
        targetSdk = project.versionCatalog.findVersion("android-sdk-compile").get().requiredVersion.toInt()
    }
}

private fun Project.configureDependencyResolutionStrategy() {
    configurations.configureEach {
        resolutionStrategy {
            // HACK: force androidx-annotation version for several modules
            //       known modules requiring the forced version: androidx-constraintlayout, androidx-core
            force(versionCatalog.findLibrary("androidx-annotation").get())

            // use the new condensed version of hamcrest
            dependencySubstitution {
                val hamcrestVersion = versionCatalog.findVersion("hamcrest").get().requiredVersion
                substitute(module("org.hamcrest:hamcrest-core"))
                    .using(module("org.hamcrest:hamcrest:$hamcrestVersion"))
                substitute(module("org.hamcrest:hamcrest-library"))
                    .using(module("org.hamcrest:hamcrest:$hamcrestVersion"))
            }
        }
    }
}

private fun BaseExtension.configureProguardRules(project: Project) {
    defaultConfig.consumerProguardFiles(project.rootProject.file("proguard-consumer-jetbrains.pro"))
}

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
// context(Project)
fun CommonExtension<*, *, *, *, *, *>.configureCompose(project: Project) {
    buildFeatures.compose = true
    project.pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    project.dependencies.apply {
        // the runtime dependency is required to build a library when compose is enabled
        addProvider("implementation", project.versionCatalog.findLibrary("androidx-compose-runtime").get())

        // these dependencies are required for tests of Composables
        addProvider("debugImplementation", project.versionCatalog.findBundle("androidx-compose-debug").get())
        addProvider("testDebugImplementation", project.versionCatalog.findBundle("androidx-compose-testing").get())
    }
}

private fun BaseExtension.configureTestOptions(project: Project) {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true

            all {
                // increase unit tests max heap size
                it.maxHeapSize = "2g"
            }
        }
    }

    // not all projects actually have tests
    project.tasks.withType<Test> {
        failOnNoDiscoveredTests.set(false)
    }
}
