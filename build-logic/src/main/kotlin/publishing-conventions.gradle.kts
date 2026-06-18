import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("maven-publish")
}

group = "org.ccci.gto.android"

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
    if (!pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
        android.publishing.singleVariant("release") {
            withSourcesJar()
        }

        afterEvaluate {
            publishing {
                publications {
                    register<MavenPublication>("release") {
                        from(components["release"])
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
