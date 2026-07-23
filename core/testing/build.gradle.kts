plugins { alias(libs.plugins.memoria.android.library) }
android { namespace = "chirag127.memoria.core.testing" }
dependencies {
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.turbine)
    implementation(libs.mockk)
}
