pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        plugins {
            id("org.jetbrains.kotlin.android") version "2.0.0"
            id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" //
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        kotlin("plugin.lombok") version "2.2.10"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "OpenFarm"
include(":app")
