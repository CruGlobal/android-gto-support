import com.android.build.gradle.api.AndroidSourceDirectorySet
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register

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

    // Android library publishing config
    pluginManager.withPlugin("com.android.library") {
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

    // Java library publishing config
    pluginManager.withPlugin("java") {
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

private val Project.isMultiplatform get() = pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")

private val Project.isSnapshotVersion get() = version.toString().endsWith("-SNAPSHOT")
