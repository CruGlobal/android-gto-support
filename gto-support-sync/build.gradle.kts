plugins {
    id("gto-support.android-conventions")
}

android.namespace = "org.ccci.gto.android.common.sync"

dependencies {
    api(project(":gto-support-core"))
    implementation(project(":gto-support-androidx-collection"))
    implementation(project(":gto-support-compat"))

    compileOnly(libs.androidx.swiperefreshlayout)
}
