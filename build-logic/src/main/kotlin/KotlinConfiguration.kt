import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension

fun KotlinTopLevelExtension.configureJvmToolchain(project: Project) {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(project.libs.findVersion("jvm").get().requiredVersion))
    }
}
