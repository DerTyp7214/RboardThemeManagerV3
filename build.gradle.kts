@file:Suppress("UNUSED_VARIABLE")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.fabric.io/public")
        maven("https://maven.google.com")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0-alpha11")
        //noinspection DifferentKotlinGradleVersion
        classpath(kotlin("gradle-plugin", version = "1.8.20-RC2"))
        classpath("com.google.gms:google-services:4.3.15")
        //classpath("com.google.protobuf:protobuf-gradle-plugin:0.9.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.fabric.io/public")
        maven("https://maven.google.com")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}
