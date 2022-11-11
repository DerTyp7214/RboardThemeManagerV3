@file:Suppress("UNUSED_VARIABLE")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("AndroidGradlePluginVersion") buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0-alpha07")
        //noinspection DifferentKotlinGradleVersion
        classpath(kotlin("gradle-plugin", version = "1.7.20"))
        classpath("com.google.gms:google-services:4.3.14")
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