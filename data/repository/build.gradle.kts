plugins {
    alias(libs.plugins.memoria.android.library)
    alias(libs.plugins.memoria.android.hilt)
}
android { namespace = "chirag127.memoria.data.repository" }
dependencies {
    implementation(project(":domain"))
    implementation(project(":data:vault"))
    implementation(project(":data:git"))
    implementation(project(":data:ai"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
}
