import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidSourceDirectorySet
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

private const val POM_SCM_CONNECTION = "scm:git:git@github.com:CruGlobal/android-gto-support.git"

fun Project.configurePublishing() {
    apply(plugin = "maven-publish")
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

                pom.withXml {
                    with(asNode().appendNode("scm")) {
                        appendNode("connection", POM_SCM_CONNECTION)
                        appendNode("developerConnection", POM_SCM_CONNECTION)
                    }
                }
            }

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
    }
}
