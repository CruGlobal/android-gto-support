plugins {
    id("gto-support.android-conventions")
}

android {
    namespace = "org.ccci.gto.android.common.viewpager"
    defaultConfig.consumerProguardFiles("proguard-consumer.pro")
}

dependencies {
    api(libs.androidx.viewpager)

    implementation(project(":gto-support-androidx-collection"))
    implementation(project(":gto-support-util"))

    implementation(libs.timber)
    implementation(libs.weakdelegate)

    compileOnly(libs.androidx.fragment)

    // region Data Binding PagerAdapter dependencies
    compileOnly(libs.androidx.databinding.runtime)
    // endregion Data Binding PagerAdapter dependencies

    // region SwipeRefreshLayoutViewPagerHelper
    compileOnly(libs.androidx.swiperefreshlayout)
    // endregion SwipeRefreshLayoutViewPagerHelper
}
