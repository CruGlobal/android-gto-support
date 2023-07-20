import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

fun Project.configureKtlint() {
    ktlint {
        version.set(libs.findVersion("ktlint").get().requiredVersion)
    }

    // HACK: workaround https://github.com/JLLeitschuh/ktlint-gradle/issues/524
    extensions.findByType<LibraryExtension>()?.sourceSets?.configureEach {
        java.srcDirs("src/$name/kotlin")
    }
}
