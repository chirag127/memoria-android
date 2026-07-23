plugins {
    alias(libs.plugins.memoria.android.library)
    alias(libs.plugins.memoria.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}
android { namespace = "chirag127.memoria.data.ai" }
dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:security"))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.ktor.client.mock)
}
