dependencies {
    api(project(":gto-support-db"))

    api(libs.kotlin.coroutines)

    // TODO: These dependencies are temporary for coroutines flow support.
    //       They should go away once we implement proper flow support
    api(project(":gto-support-db-livedata"))
    api(libs.androidx.lifecycle.livedata.ktx)
}
