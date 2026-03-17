import org.gradle.api.Project

fun Project.configureKtlint() {
    ktlint {
        version.set(versionCatalog.findVersion("ktlint").get().requiredVersion)
    }

    dependencies.add("ktlintRuleset", versionCatalog.findBundle("ktlint-rulesets").get())
}
