import org.gradle.api.Project

val Project.isSnapshotVersion get() = version.toString().endsWith("-SNAPSHOT")
