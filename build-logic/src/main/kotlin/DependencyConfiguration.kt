import org.gradle.api.Project

internal fun Project.baseDependencyResolutionStrategy() {
    configurations.configureEach {
        resolutionStrategy {
            // HACK: force androidx-annotation version for several modules
            //       known modules requiring the forced version: androidx-constraintlayout, androidx-core
            force(versionCatalog.findLibrary("androidx-annotation").get())

            // use the new condensed version of hamcrest
            dependencySubstitution {
                val hamcrestVersion = versionCatalog.findVersion("hamcrest").get().requiredVersion
                substitute(module("org.hamcrest:hamcrest-core"))
                    .using(module("org.hamcrest:hamcrest:$hamcrestVersion"))
                substitute(module("org.hamcrest:hamcrest-library"))
                    .using(module("org.hamcrest:hamcrest:$hamcrestVersion"))
            }
        }
    }
}
