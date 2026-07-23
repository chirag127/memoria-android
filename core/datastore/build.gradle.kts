plugins { alias(libs.plugins.memoria.android.library) }
android { namespace = "chirag127.memoria.core.datastore" }
dependencies {
    implementation(libs.datastore.preferences)
    implementation(project(":core:common"))
}
