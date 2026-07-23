plugins { alias(libs.plugins.memoria.android.library) }
android {
    namespace = "chirag127.memoria.data.vault"
    testOptions.unitTests.all { it.useJUnitPlatform() }
}
dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(libs.kotlinx.datetime)

    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
}
