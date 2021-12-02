import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.dagger.hilt.androidGradle)
    }
}
plugins {
    alias(libs.plugins.junitJacoco)
    alias(libs.plugins.ktlint)
}

allprojects {
    version = "3.10.1"

    repositories {
        maven {
            setUrl("https://jitpack.io")
            content { includeGroupByRegex("com\\.github\\..*") }
        }
        google()
        mavenCentral()
        jcenter {
            content { includeModule("io.realm", "android-adapters") }
        }
    }

    afterEvaluate {
        configurations.all {
            resolutionStrategy {
                force(libs.androidx.annotation)
                force(libs.androidx.core)
                force(libs.kotlin.coroutines)
                force(libs.kotlin.stdlib.jdk8)
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

// region jacoco
junitJacoco {
    jacocoVersion = libs.versions.jacoco.get()
    includeNoLocationClasses = true
}
allprojects {
    afterEvaluate {
        tasks.withType<Test> {
            extensions.configure<JacocoTaskExtension> {
                excludes = listOf("jdk.internal.*")
            }
        }
    }
}
tasks.register("jacocoTestReport") {
    subprojects.forEach { dependsOn(it.tasks.withType<JacocoReport>()) }
}
allprojects {
    if (gradle.startParameter.excludedTaskNames.contains("test")) {
        // exclude all test type tasks when the test task is excluded
        tasks.withType<Test>().configureEach {
            gradle.startParameter.excludedTaskNames += name
        }
    }
}
// endregion jacoco

// region ktlint
allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        android.set(true)
        reporters {
            reporter(ReporterType.PLAIN_GROUP_BY_FILE)
            reporter(ReporterType.CHECKSTYLE)
        }
    }
}
// endregion ktlint
