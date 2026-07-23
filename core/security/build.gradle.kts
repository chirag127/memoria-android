plugins { alias(libs.plugins.memoria.android.library) }
android { namespace = "chirag127.memoria.core.security" }
dependencies {
    implementation(libs.androidx.security.crypto)
    implementation(project(":core:common"))
}
