plugins {
    id("build-logic")
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
                force(libs.androidx.annotation)
                force(libs.androidx.appcompat)
                force(libs.androidx.core)
                force(libs.androidx.lifecycle.livedata.core)
                force(libs.androidx.sqlite)
                force(libs.kotlin.coroutines)
                force(libs.okio)

                dependencySubstitution {
                    // use the new condensed version of hamcrest
                    substitute(module("org.hamcrest:hamcrest-core"))
                        .using(module("org.hamcrest:hamcrest:${libs.versions.hamcrest.get()}"))
                    substitute(module("org.hamcrest:hamcrest-library"))
                        .using(module("org.hamcrest:hamcrest:${libs.versions.hamcrest.get()}"))
                }
            }
        }

        if (extensions.findByType<com.android.build.gradle.BaseExtension>() != null) {
            dependencies {
                add("implementation", libs.kotlin.stdlib)

                add("compileOnly", libs.androidx.annotation)

                add("testImplementation", libs.androidx.test.junit)
                add("testImplementation", libs.junit)
                add("testImplementation", libs.mockito)
                add("testImplementation", libs.mockito.kotlin)
                add("testImplementation", libs.mockk)
                add("testImplementation", libs.robolectric)
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
