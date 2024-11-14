// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version by extra("2.0.20")
    val kotlinx_serialization by extra("1.6.3")

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.49")
        classpath("com.google.firebase:firebase-appdistribution-gradle:5.0.0")
    }
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10" apply false
    id("com.google.dagger.hilt.android") version "2.49" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false
}

allprojects {

}