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
        maven("https://jitpack.io")
        maven {
            val gitHubGradleAccessToken:String? by settings
            url = uri("https://raw.githubusercontent.com/Allegion-Plc/schlage-mobile-credential-android-sdk/master/releases")
            credentials(HttpHeaderCredentials::class) {
                name = "Authorization"
                value = "Bearer $gitHubGradleAccessToken"
            }
            authentication {
                create("header", HttpHeaderAuthentication::class)
            }
        }
    }
}

rootProject.name = "mobile-sdk-android-samples"
include(
    ":app-public",
    ":common-app"
)
