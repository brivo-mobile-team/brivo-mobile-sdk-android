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
include(
    ":app-kotlin",
    ":app-compose"
)

include(
    ":brivoaccess",
    ":brivoble",
    ":brivoble-core",
    ":brivoconfiguration",
    ":brivocore",
    ":brivolocalauthentication",
    ":brivoonair",
    ":brivosmarthome"
)
project(":brivoaccess").projectDir = File(rootDir, "mobile-sdk-android/brivoaccess")
project(":brivoble").projectDir = File(rootDir, "mobile-sdk-android/brivoble")
project(":brivoble-core").projectDir = File(rootDir, "mobile-sdk-android/brivoble-core")
project(":brivoconfiguration").projectDir = File(rootDir, "mobile-sdk-android/brivoconfiguration")
project(":brivocore").projectDir = File(rootDir, "mobile-sdk-android/brivocore")
project(":brivolocalauthentication").projectDir = File(rootDir, "mobile-sdk-android/brivolocalauthentication")
project(":brivoonair").projectDir = File(rootDir, "mobile-sdk-android/brivoonair")
project(":brivosmarthome").projectDir = File(rootDir, "mobile-sdk-android/brivosmarthome")
