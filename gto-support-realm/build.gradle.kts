plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.realm"
}

dependencies {
    api(libs.realm)

    // region RecyclerViewAdapter dependencies
    compileOnly(project(":gto-support-recyclerview"))
    compileOnly(libs.androidx.databinding.runtime)
    compileOnly(libs.androidx.lifecycle.livedata.core)
    implementation(libs.weakdelegate)
    // endregion RecyclerViewAdapter dependencies
}
