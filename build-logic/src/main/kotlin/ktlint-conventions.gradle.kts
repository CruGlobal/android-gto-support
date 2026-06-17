plugins {
    id("org.jlleitschuh.gradle.ktlint")
}

ktlint {
    version.set(versionCatalog.findVersion("ktlint").get().requiredVersion)
}

dependencies {
    ktlintRuleset(versionCatalog.findBundle("ktlint-rulesets").get())
}
