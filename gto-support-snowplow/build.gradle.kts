dependencies {
    api(libs.snowplow)

    compileOnly(libs.timber)

    testImplementation(libs.kotlin.coroutines)
    testImplementation(libs.timber)
}
