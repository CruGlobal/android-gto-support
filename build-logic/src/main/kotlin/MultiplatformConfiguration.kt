import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.configureIosTarget() {
    iosArm64()
    iosSimulatorArm64()
}

fun KotlinMultiplatformExtension.configureJsTarget() {
    js {
        binaries.library()
        browser {
            testTask { useMocha() }
        }
        nodejs()
    }
}

fun KotlinMultiplatformExtension.configureJvmTarget() {
    jvm()
}
