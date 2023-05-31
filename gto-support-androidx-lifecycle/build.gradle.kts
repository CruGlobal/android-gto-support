plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.androidx.lifecycle"
    configureCompose(project)
    testFixtures.enable = true
}

dependencies {
    api(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.weakdelegate)

    // region Composables
    compileOnly(libs.androidx.compose.ui)
    // endregion Composables

    // region ObservableLiveData
    compileOnly(libs.androidx.databinding.runtime)
    // endregion ObservableLiveData

    // region SavedStateHandle
    compileOnly(libs.androidx.lifecycle.viewmodel.savedstate)
    // endregion SavedStateHandle

    testFixturesApi(libs.androidx.lifecycle.viewmodel)

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.core.ktx)
    testImplementation(libs.androidx.lifecycle.runtime.testing)
    testImplementation(libs.androidx.lifecycle.viewmodel.savedstate)
    testImplementation(libs.kotlin.coroutines.test)
}
