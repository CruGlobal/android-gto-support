import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

fun Project.configureCheckstyle(version: String = "7.8.2") {
    apply(plugin = "checkstyle")
    extensions.configure<CheckstyleExtension> {
        toolVersion = version
    }
    val task = tasks.register<Checkstyle>("checkstyle") {
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")
        setSource("src")
        include("*/java/**/*.java")
        ignoreFailures = false
        isShowViolations = true
        classpath = files()
    }
    afterEvaluate { tasks.findByName("check")?.dependsOn(task) }
}
