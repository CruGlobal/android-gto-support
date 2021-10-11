import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

@JvmOverloads
fun Project.configureAndroidLibrary(block: LibraryExtension.() -> Unit = {}) {
    extensions.configure<LibraryExtension> {
        configureSdk()
        configureProguardRules()
        configureCompilerOptions()
        configureTestOptions()

        block()
    }

    configurePublishing()
}

private fun BaseExtension.configureSdk() {
    compileSdkVersion(30)

    defaultConfig {
        minSdk = 21
        targetSdk = 30
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
    (this as ExtensionAware).extensions.configure<KotlinJvmOptions> {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs += "-Xjvm-default=all"
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
