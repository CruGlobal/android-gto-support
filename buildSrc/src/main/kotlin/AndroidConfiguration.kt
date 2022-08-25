import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

private const val GROUP = "org.ccci.gto.android"
private const val GROUP_TESTING = "org.ccci.gto.android.testing"

fun Project.configureAndroidLibrary() {
    group = GROUP
    extensions.configure<LibraryExtension> {
        configureSdk()
        configureProguardRules()
        configureCompilerOptions()
        configureTestOptions()
    }

    configureKotlinKover()
    configurePublishing()
}

fun Project.configureAndroidTestingLibrary() {
    configureAndroidLibrary()
    group = GROUP_TESTING
}

private fun BaseExtension.configureSdk() {
    compileSdkVersion(33)

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}

private fun Project.configureProguardRules() {
    extensions.configure<BaseExtension> {
        defaultConfig.consumerProguardFiles(rootProject.file("proguard-consumer-jetbrains.pro"))
    }
}

private fun BaseExtension.configureCompilerOptions() {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    (this as ExtensionAware).extensions.findByType<KotlinJvmOptions>()?.apply {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs += "-Xjvm-default=all"
    }
}

// TODO: provide Project using the new multiple context receivers functionality.
//       this is prototyped in 1.6.20 and will probably reach beta in Kotlin 1.8 or 1.9
//context(Project)
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
