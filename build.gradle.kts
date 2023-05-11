@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("com.google.devtools.ksp") version "1.8.21-1.0.11" apply false
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("AndroidGradlePluginVersion") buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0-alpha03")
        //noinspection DifferentKotlinGradleVersion
        classpath(kotlin("gradle-plugin", version = "1.8.0"))
        classpath("com.google.gms:google-services:4.3.15")
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

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}