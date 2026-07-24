plugins {
    alias(libs.plugins.memoria.android.library)
    alias(libs.plugins.memoria.android.hilt)
}
android { namespace = "chirag127.memoria.core.datastore" }
dependencies {
    implementation(libs.datastore.preferences)
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
}
