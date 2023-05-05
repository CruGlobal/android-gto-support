import org.gradle.api.Project

internal fun Project.configureKotlinKover() {
    plugins.apply("org.jetbrains.kotlinx.kover")
}
