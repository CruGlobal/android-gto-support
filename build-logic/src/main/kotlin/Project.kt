import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jlleitschuh.gradle.ktlint.KtlintExtension

internal val Project.libs get() = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val Project.android: LibraryExtension get() = extensions.getByType()
internal val Project.androidComponents
    get() = project.extensions.getByName("androidComponents") as AndroidComponentsExtension<*, *, *>
internal val Project.java: JavaPluginExtension get() = extensions.getByType()

internal fun Project.android(action: LibraryExtension.() -> Unit) = extensions.configure(action)
internal fun Project.java(action: JavaPluginExtension.() -> Unit) = extensions.configure(action)
internal fun Project.ktlint(action: KtlintExtension.() -> Unit) = extensions.configure(action)
internal fun Project.publishing(action: PublishingExtension.() -> Unit) = extensions.configure(action)
