plugins {
    alias(libs.plugins.memoria.android.library)
    alias(libs.plugins.memoria.android.room)
    alias(libs.plugins.memoria.android.hilt)
}
android { namespace = "chirag127.memoria.core.database" }
dependencies { implementation(project(":core:common")) }
