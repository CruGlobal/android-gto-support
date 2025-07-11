import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
// context(Project)
internal fun LibraryExtension.baseConfiguration(project: Project) {
    configureSdk()
    configureProguardRules(project)
    configureTestOptions(project)
}

private fun BaseExtension.configureSdk() {
    compileSdkVersion(35)

    defaultConfig {
        minSdk = 21
        targetSdk = 35
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

    // Test Sharding
    val shard = project.findProperty("testShard")?.toString()?.toIntOrNull()
    val totalShards = project.findProperty("testTotalShards")?.toString()?.toIntOrNull()
    if (shard != null && totalShards != null) {
        if (Math.floorMod(project.path.hashCode(), totalShards) != Math.floorMod(shard, totalShards)) {
            project.androidComponents.beforeVariants { it.enableUnitTest = false }
        }
    }
}
