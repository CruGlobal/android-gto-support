import org.gradle.api.Project

fun Project.configureKtlint() {
    ktlint {
        version.set(libs.findVersion("ktlint").get().requiredVersion)
    }

    dependencies.add("ktlintRuleset", libs.findBundle("ktlint-rulesets").get())
}
