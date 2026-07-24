import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Use Room via KSP only (no Room Gradle plugin) — the plugin requires a
            // room{} schemaDirectory DSL + its own classpath dep. KSP setup is enough
            // for the scaffold; add the Gradle plugin later if schema export is needed.
            pluginManager.apply("com.google.devtools.ksp")
            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
                arg("room.schemaLocation", "$projectDir/schemas")
            }
            dependencies {
                add("implementation", libs.findLibrary("room-runtime").get())
                add("implementation", libs.findLibrary("room-ktx").get())
                add("ksp", libs.findLibrary("room-compiler").get())
            }
        }
    }
}
