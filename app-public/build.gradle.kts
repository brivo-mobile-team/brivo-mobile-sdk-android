plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}
val CLIENT_ID_PROD: String by properties
val CLIENT_SECRET_PROD: String by properties
val CLIENT_ID_EU: String by properties
val CLIENT_SECRET_EU: String by properties
val gitHubGradleAccessToken: String? by properties

val VERSION_NAME: String? by properties

android {
    namespace = "com.brivo.app_sdk_public"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.brivo.app_sdk_public"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = VERSION_NAME?.removeSurrounding("\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "CLIENT_ID", CLIENT_ID_PROD)
        buildConfigField("String", "CLIENT_SECRET", CLIENT_SECRET_PROD)
        buildConfigField("String", "CLIENT_ID_EU", CLIENT_ID_EU)
        buildConfigField("String", "CLIENT_SECRET_EU", CLIENT_SECRET_EU)
    }

    signingConfigs {
        create("release") {
            storeFile = file("../brivo.keystore")
            storePassword = "android"
            keyAlias = "mobile-sdk-sample"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    composeCompiler {
        includeSourceInformation = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    packaging {
        jniLibs.pickFirsts.add("/**/libbrivoonair-lib.so")
    }

}

fun checkGithubAccessToken(gitHubGradleAccessToken: String?): Boolean =
    gitHubGradleAccessToken.isNullOrEmpty().not()

val brivoSdkVersion: String by rootProject.extra

dependencies {

    if (checkGithubAccessToken(gitHubGradleAccessToken)) {
        // Allegion SDK Module
        implementation("com.allegion:MobileAccessSDK:5.0.1")
    }
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoble-allegion:$brivoSdkVersion")

    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoaccess:$brivoSdkVersion")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoble:$brivoSdkVersion")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoblecore:$brivoSdkVersion")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivocore:$brivoSdkVersion")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivolocalauthentication:$brivoSdkVersion")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoonair:$brivoSdkVersion")

    implementation(project(":common-app"))


//    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivo-hidorigo:$brivo_sdk_version")
//    debugImplementation (files("../brivo-hidorigo/lib/origo-sdk-debug-3.4.0.aar"))
//    releaseImplementation (files("../brivo-hidorigo/lib/origo-sdk-release-3.4.0.aar"))

    implementation(platform("androidx.compose:compose-bom:2025.06.01"))
    implementation("com.karumi:dexter:6.2.3")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material:1.8.3")
    implementation("androidx.navigation:navigation-compose:2.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.dagger:hilt-android:2.56.2")
    ksp("com.google.dagger:hilt-compiler:2.56.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    debugImplementation("androidx.compose.ui:ui-tooling:1.8.3")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.8.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.8.3")

    testImplementation("junit:junit:4.13.2")
}
