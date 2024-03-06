pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") {
            credentials {
                username = "jp_kplk7bb17sp89939biabtovlu5"
            }
        }
    }
}

rootProject.name = "mobile-sdk-android-samples"
include(":app-kotlin")
include(":app-compose")
