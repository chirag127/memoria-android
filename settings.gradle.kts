pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "memoria"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":core:common")
include(":core:model")
include(":core:ui")
include(":core:testing")
include(":core:database")
include(":core:datastore")
include(":core:security")
include(":domain")
include(":data:vault")
include(":data:git")
include(":data:ai")
include(":data:repository")
include(":feature:capture")
include(":feature:timeline")
include(":feature:search")
include(":feature:settings")
