plugins {
    id("build-logic")
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ktlint)
}

allprojects {
    // configure the project version
    if (!project.findProperty("releaseBuild")?.toString().toBoolean()) {
        project.findProperty("versionSuffix")?.toString()
            ?.takeIf { it.matches(Regex("\\S+")) }
            ?.let { version = "$version-$it" }
        version = "$version-SNAPSHOT"
    }

    afterEvaluate {
        configurations.all {
            resolutionStrategy {
                force(libs.androidx.core)
            }
        }
    }
}

// region checkstyle
allprojects {
    afterEvaluate {
        apply(plugin = "checkstyle")
        extensions.configure<CheckstyleExtension> {
            toolVersion = libs.versions.checkstyle.get()
        }
        val task = tasks.register<Checkstyle>("checkstyle") {
            configFile = rootProject.file("config/checkstyle/checkstyle.xml")
            setSource("src")
            include("*/java/**/*.java")
            ignoreFailures = false
            isShowViolations = true
            classpath = files()
        }
        tasks.findByName("check")?.dependsOn(task)
    }
}
// endregion checkstyle

configureKtlint()
