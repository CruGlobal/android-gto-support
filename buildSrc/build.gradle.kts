import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlinter)
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
    implementation(libs.kotlinter)
}

kotlin.jvmToolchain {
    languageVersion.set(libs.versions.jvm.map { JavaLanguageVersion.of(it) })
}

// region Kotlinter
tasks.register<LintTask>("lintKotlinDslBuildScripts") {
    source(file("build.gradle.kts"))
    source(file("settings.gradle.kts"))
}.also { tasks.named("lintKotlin") { dependsOn(it) } }
// endregion Kotlinter
