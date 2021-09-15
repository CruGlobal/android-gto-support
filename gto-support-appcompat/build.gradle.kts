android {
    defaultConfig {
        consumerProguardFiles("proguard-consumer.pro")
    }
}

dependencies {
    implementation(project(":gto-support-core"))
    implementation(project(":gto-support-util"))

    implementation(libs.androidx.appcompat)

    implementation(libs.timber)
}
