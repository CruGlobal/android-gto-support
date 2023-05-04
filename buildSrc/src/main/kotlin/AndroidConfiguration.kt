import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
// context(Project)
internal fun LibraryExtension.baseConfiguration(project: Project) {
    configureSdk()
    configureProguardRules(project)
    configureCompilerOptions(project)
    configureTestOptions()
}

private fun BaseExtension.configureSdk() {
    compileSdkVersion(33)

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}

private fun BaseExtension.configureProguardRules(project: Project) {
    defaultConfig.consumerProguardFiles(project.rootProject.file("proguard-consumer-jetbrains.pro"))
}

private fun BaseExtension.configureCompilerOptions(project: Project) {
    compileOptions {
        // HACK: workaround a kotlin.jvmToolchain bug
        //       see: https://issuetracker.google.com/issues/260059413
        sourceCompatibility = JavaVersion.toVersion(project.libs.findVersion("jvm").get().requiredVersion)
        targetCompatibility = JavaVersion.toVersion(project.libs.findVersion("jvm").get().requiredVersion)
    }
    (this as ExtensionAware).extensions.findByType<KotlinJvmOptions>()?.apply {
        freeCompilerArgs += "-Xjvm-default=all"
    }
}

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
// context(Project)
fun CommonExtension<*, *, *, *>.configureCompose(project: Project) {
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion =
        project.libs.findVersion("androidx-compose-compiler").get().requiredVersion

    project.dependencies.apply {
        // the runtime dependency is required to build a library when compose is enabled
        addProvider("implementation", project.libs.findLibrary("androidx-compose-runtime").get())

        // these dependencies are required for tests of Composables
        addProvider("debugImplementation", project.libs.findBundle("androidx-compose-debug").get())
        addProvider("testDebugImplementation", project.libs.findBundle("androidx-compose-testing").get())
    }
}

private fun BaseExtension.configureTestOptions() {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true

            all {
                // increase unit tests max heap size
                it.jvmArgs("-Xmx2g")
            }
        }
    }
}
