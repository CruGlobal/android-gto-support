import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal fun NamedDomainObjectContainer<KotlinSourceSet>.androidHostTest(action: KotlinSourceSet.() -> Unit) =
    named("androidHostTest").configure(action)
