plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}
val CLIENT_ID: String by properties
val CLIENT_SECRET: String by properties
val CLIENT_ID_EU: String by properties
val CLIENT_SECRET_EU: String by properties
val gitHubGradleAccessToken: String? by properties

android {
    namespace = "com.brivo.app_sdk_public"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.brivo.app_sdk_public"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.22.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "CLIENT_ID", CLIENT_ID)
        buildConfigField("String", "CLIENT_SECRET", CLIENT_SECRET)
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kapt {
        correctErrorTypes = true
    }
}

fun checkGithubAccessToken(gitHubGradleAccessToken: String?): Boolean =
    gitHubGradleAccessToken.isNullOrEmpty().not()

dependencies {

    val brivo_sdk_version = "1.22.0"
    if (checkGithubAccessToken(gitHubGradleAccessToken)) {
        // Allegion SDK Module
        implementation("com.allegion:MobileAccessSDK:latest.release")
    }
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoble-allegion:$brivo_sdk_version")

    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoaccess:$brivo_sdk_version")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoble:$brivo_sdk_version")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoblecore:$brivo_sdk_version")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivocore:$brivo_sdk_version")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivolocalauthentication:$brivo_sdk_version")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoonair:$brivo_sdk_version")

    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("com.karumi:dexter:6.2.3")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material:1.6.8")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-compiler:2.49")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")

    testImplementation("junit:junit:4.13.2")
}