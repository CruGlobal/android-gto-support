import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

fun Project.configureKotlinter() {
    tasks.register<LintTask>("lintKotlinDslBuildScripts") { source(buildScripts) }
        .also { tasks.named("lintKotlin") { dependsOn(it) } }
    tasks.register<FormatTask>("formatKotlinDslBuildScripts") { source(buildScripts) }
        .also { tasks.named("formatKotlin") { dependsOn(it) } }
}

private val Project.buildScripts get() = layout.projectDirectory.asFileTree.matching { include("*.gradle.kts") }
