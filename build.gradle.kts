@file:Suppress("UNUSED_VARIABLE")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.0-alpha02")
        //noinspection DifferentKotlinGradleVersion
        classpath(kotlin("gradle-plugin", version = "1.5.31"))
        classpath("com.google.gms:google-services:4.3.10")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}