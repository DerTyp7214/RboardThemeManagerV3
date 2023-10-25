plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinAndroid) apply false
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("AndroidGradlePluginVersion") buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle)
        //noinspection DifferentKotlinGradleVersion
        classpath(kotlin("gradle-plugin", version = "1.9.10"))
        classpath(libs.google.services)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven ("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}