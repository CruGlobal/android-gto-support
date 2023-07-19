import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlinter)
}

kotlin.jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}

gradlePlugin {
    plugins.register("build-logic") {
        id = "build-logic"
        implementationClass = "org.ccci.gto.android.common.gradle.BuildLogicPlugin"
    }
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.kotlin.kover.gradlePlugin)
    implementation(libs.kotlinter)
}

// region Kotlinter
tasks.register<LintTask>("lintKotlinDslBuildScripts") {
    source(file("build.gradle.kts"))
    source(file("settings.gradle.kts"))
}.also { tasks.named("lintKotlin") { dependsOn(it) } }
// endregion Kotlinter
