plugins {
    alias(libs.plugins.memoria.android.library)
    alias(libs.plugins.memoria.android.hilt)
}
android { namespace = "chirag127.memoria.data.git" }
dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:security"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(libs.jgit)
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
}
