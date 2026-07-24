import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention for presentation feature modules: Android library + Compose + Hilt +
 * the shared UI/domain deps every feature needs. Keeps feature build files ~3 lines.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("memoria.android.library")
        pluginManager.apply("memoria.android.compose")
        pluginManager.apply("memoria.android.hilt")
        dependencies {
            add("implementation", project(":domain"))
            add("implementation", project(":core:ui"))
            add("implementation", project(":core:common"))
            add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())
            add("implementation", libs.findLibrary("androidx-navigation-compose").get())
            add("implementation", libs.findLibrary("hilt-navigation-compose").get())
            add("testImplementation", project(":core:testing"))
        }
    }
}
