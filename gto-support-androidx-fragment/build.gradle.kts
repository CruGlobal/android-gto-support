plugins {
    id("gto-support.android-conventions")
}

android {
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(libs.androidx.fragment)
    implementation(project(":gto-support-util"))
    compileOnly(libs.androidx.fragment.ktx)

    // DataBindingDialogFragment dependencies
    compileOnly(libs.androidx.appcompat)
    compileOnly(libs.androidx.databinding.runtime)
}
