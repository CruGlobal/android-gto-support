import org.gradle.api.Project

fun Project.configureKotlinKover() {
    plugins.apply("org.jetbrains.kotlinx.kover")
}
