android {
    defaultConfig.consumerProguardFile("proguard-consumer-tablayout.pro")

    buildFeatures.dataBinding = true
    dataBinding.addDefaultAdapters = false
}

dependencies {
    implementation(project(":gto-support-util"))

    api(libs.materialComponents)

    compileOnly(libs.androidx.databinding.adapters)
    compileOnly(libs.androidx.databinding.runtime)
}
