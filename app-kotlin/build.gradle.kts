plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.demo.sample.kotlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.demo.sample.kotlin"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    releaseImplementation("org.bitbucket.brivoinc:mobile-sdk-android:feature~ME-517-SNAPSHOT")

    debugImplementation(project(":brivoaccess"))
    debugImplementation(project(":brivoble"))
    debugImplementation(project(":brivoble-core"))
    debugImplementation(project(":brivoconfiguration"))
    debugImplementation(project(":brivocore"))
    debugImplementation(project(":brivolocalauthentication"))
    debugImplementation(project(":brivoonair"))
    debugImplementation(project(":brivosmarthome"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("pl.bclogic:pulsator4droid:1.0.3")
    implementation("com.karumi:dexter:6.2.3")
    implementation("com.google.code.gson:gson:2.10.1")
}