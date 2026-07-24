plugins {
    alias(libs.plugins.memoria.android.library)
    alias(libs.plugins.memoria.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "chirag127.memoria.data.ai"
    testOptions.unitTests.all { it.useJUnitPlatform() }
}
dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:security"))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
}
