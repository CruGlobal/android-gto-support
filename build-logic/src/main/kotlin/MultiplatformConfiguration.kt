import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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
    iosArm64()
    iosX64()
    iosSimulatorArm64()
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
