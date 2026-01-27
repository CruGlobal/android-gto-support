import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
// context(Project)
internal fun BaseExtension.baseConfiguration(project: Project) {
    configureSdk(project)
    configureProguardRules(project)
    configureTestOptions(project)
    project.configureCommonDependencies()
}

private fun BaseExtension.configureSdk(project: Project) {
    compileSdkVersion(project.libs.findVersion("android-sdk-compile").get().requiredVersion.toInt())

    defaultConfig {
        minSdk = project.libs.findVersion("android-sdk-min").get().requiredVersion.toInt()
        targetSdk = project.libs.findVersion("android-sdk-compile").get().requiredVersion.toInt()
    }
}

private fun Project.configureCommonDependencies() {
    // HACK: sync kotlin-metadata-jvm version for Dagger
    //       This works around dagger/hilt depending on an older version when upgrading to Kotlin 2.3.0.
    //       This can be removed when Dagger/Hilt is upgraded and the the build completes successfully without this
    //       override.
    configurations.configureEach {
        resolutionStrategy.force(libs.findLibrary("kotlin-metadata-jvm").get())
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
        addProvider("implementation", project.libs.findLibrary("androidx-compose-runtime").get())

        // these dependencies are required for tests of Composables
        addProvider("debugImplementation", project.libs.findBundle("androidx-compose-debug").get())
        addProvider("testDebugImplementation", project.libs.findBundle("androidx-compose-testing").get())
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

    // Test Sharding
    val shard = project.findProperty("testShard")?.toString()?.toIntOrNull()
    val totalShards = project.findProperty("testTotalShards")?.toString()?.toIntOrNull()
    if (shard != null && totalShards != null) {
        if (Math.floorMod(project.path.hashCode(), totalShards) != Math.floorMod(shard, totalShards)) {
            project.androidComponents.beforeVariants { it.enableUnitTest = false }
        }
    }
}
