plugins { alias(libs.plugins.memoria.jvm.library) }
dependencies {
    api(project(":core:model"))
    api(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
}
