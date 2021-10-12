plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle)
}
