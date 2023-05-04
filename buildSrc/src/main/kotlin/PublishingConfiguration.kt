import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.AndroidSourceDirectorySet
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

private const val GROUP = "org.ccci.gto.android"
internal const val GROUP_TESTING = "org.ccci.gto.android.testing"

internal fun Project.configurePublishing() {
    group = GROUP
    publishing {
        repositories {
            maven {
                name = "cruGlobalMavenRepository"
                setUrl(
                    when {
                        isSnapshotVersion ->
                            "https://cruglobal.jfrog.io/cruglobal/list/maven-cru-android-public-snapshots-local/"
                        else -> "https://cruglobal.jfrog.io/cruglobal/list/maven-cru-android-public-releases-local/"
                    }
                )

                credentials(PasswordCredentials::class)
            }
        }
    }

    when {
        isAndroidLibrary -> {
            // not Kotlin Multiplatform, so configure the android publication
            if (!isMultiplatform) {
                android.publishing.singleVariant("release")

                val sourcesJar = tasks.register<Jar>("sourcesJar") {
                    with(android.sourceSets["main"]) {
                        from(java.srcDirs, (kotlin as AndroidSourceDirectorySet).srcDirs)
                    }
                }

                afterEvaluate {
                    publishing {
                        publications {
                            register<MavenPublication>("release") {
                                from(components["release"])

                                artifact(sourcesJar) {
                                    classifier = "sources"
                                }
                            }
                        }
                    }
                }
            }
        }
        isJava -> {
            java {
                withJavadocJar()
                withSourcesJar()
            }

            publishing {
                publications {
                    register<MavenPublication>("library") {
                        from(components["java"])
                    }
                }
            }
        }
    }
}

private val Project.isAndroidLibrary get() = extensions.findByType<LibraryExtension>() != null
private val Project.isJava get() = extensions.findByType<JavaPluginExtension>() != null
private val Project.isMultiplatform get() = extensions.findByType<KotlinMultiplatformExtension>() != null
