plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()

dependencies {
    api(libs.realm)

    // region RecyclerViewAdapter dependencies
    compileOnly(project(":gto-support-recyclerview"))
    compileOnly(libs.androidx.databinding.runtime)
    compileOnly(libs.androidx.lifecycle.livedata.core)
    compileOnly(libs.realm.adapters)
    implementation(libs.weakdelegate)
    // endregion RecyclerViewAdapter dependencies
}
