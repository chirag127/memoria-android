import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal val Project.libs: org.gradle.api.artifacts.VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal const val COMPILE_SDK = 35
internal const val MIN_SDK = 26
internal const val TARGET_SDK = 35

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = COMPILE_SDK
        defaultConfig { minSdk = MIN_SDK }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
    extensions.getByType<KotlinAndroidProjectExtension>().apply {
        jvmToolchain(17)
        compilerOptions {
            freeCompilerArgs.add("-Xannotation-default-target=param-property")
        }
    }
}

internal fun Project.configureKotlinJvm() {
    extensions.getByType<JavaPluginExtension>().apply {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    extensions.getByType<KotlinJvmProjectExtension>().apply {
        jvmToolchain(17)
    }
}
