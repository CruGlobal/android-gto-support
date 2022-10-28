import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.AndroidSourceDirectorySet
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

private const val GROUP = "org.ccci.gto.android"
private const val GROUP_TESTING = "org.ccci.gto.android.testing"

fun Project.configurePublishing() {
    apply(plugin = "maven-publish")
    group = GROUP
    extensions.configure<PublishingExtension> {
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

    // not Kotlin Multiplatform, so configure the android publication
    if (extensions.findByType<KotlinMultiplatformExtension>() == null) {
        extensions.configure<LibraryExtension> {
            publishing.singleVariant("release")
        }

        val sourcesJar = tasks.register<Jar>("sourcesJar") {
            with(project.extensions.getByType<BaseExtension>().sourceSets["main"]) {
                from(java.srcDirs, (kotlin as AndroidSourceDirectorySet).srcDirs)
            }
        }

        afterEvaluate {
            extensions.configure<PublishingExtension> {
                publications.register<MavenPublication>("release") {
                    from(components["release"])

                    artifact(sourcesJar) {
                        classifier = "sources"
                    }
                }
            }
        }
    }
}

fun Project.overridePublishingGroupForTestFixtureProject() {
    group = GROUP_TESTING
}
