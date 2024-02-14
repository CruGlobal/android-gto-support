import org.gradle.api.Project

fun Project.configureKtlint() {
    ktlint {
        version.set(libs.findVersion("ktlint").get().requiredVersion)
    }
}
