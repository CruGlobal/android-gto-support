plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.ktlint)
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

ktlint {
    version.set(libs.versions.ktlint)

    filter {
        exclude { it.file.path.startsWith("${buildDir.path}/") }
    }
}
