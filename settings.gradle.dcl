pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

dependencyResolutionManagement {
    repositoriesMode = FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "oop-lab-ws17"

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
