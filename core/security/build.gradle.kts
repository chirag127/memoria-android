plugins {
    alias(libs.plugins.memoria.android.library)
    alias(libs.plugins.memoria.android.hilt)
}
android { namespace = "chirag127.memoria.core.security" }
dependencies {
    implementation(libs.androidx.security.crypto)
    implementation(project(":core:common"))
}
