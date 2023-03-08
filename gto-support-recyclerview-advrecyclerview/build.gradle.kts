plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.recyclerview.advrecyclerview"

dependencies {
    api(libs.advancedrecyclerview)
    api(libs.androidx.recyclerview)

    implementation(libs.androidx.appcompat)
    implementation(libs.weakdelegate)

    // region Data Binding adapters
    compileOnly(project(":gto-support-recyclerview"))
    compileOnly(libs.androidx.databinding.runtime)
    // endregion Data Binding adapters
}
