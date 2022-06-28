plugins {
    id("com.android.library")
    kotlin("android")
}

configureAndroidLibrary()
android {
    configureCompose(project)
}

dependencies {
    api(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.weakdelegate)

    // region Composables
    implementation(libs.androidx.compose.runtime)
    compileOnly(libs.androidx.compose.ui)
    // endregion Composables

    // region ObservableLiveData
    compileOnly(libs.androidx.databinding.runtime)
    // endregion ObservableLiveData

    // region SavedStateHandle
    compileOnly(libs.androidx.lifecycle.viewmodel.savedstate)
    // endregion SavedStateHandle

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.core.ktx)
    testImplementation(libs.androidx.lifecycle.runtime.testing)
    testImplementation(libs.androidx.lifecycle.viewmodel.savedstate)
    testImplementation(libs.kotlin.coroutines.test)
}
