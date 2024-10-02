@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")

    alias(libs.plugins.ksp)
}

android {
    buildToolsVersion = "35.0.0"
    compileSdk = 35
    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true
    buildFeatures.buildConfig = true

    namespace = "de.dertyp7214.rboardthememanager"

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthememanager"
        minSdk = 31
        targetSdk = 35
        versionCode = 393002
        versionName = "3.9.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations += listOf(
            "ar", "cs", "da", "de",
            "el", "en", "es", "fi",
            "fr", "hi", "hu", "in",
            "it", "ja", "nl", "no",
            "pl", "pt-rBR", "ro", "ru",
            "sv", "uk", "vi",
            "zh-rCN", "zh-rTW"
        )
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_22
        targetCompatibility = JavaVersion.VERSION_22
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_22.toString()
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
            "-Xsuppress-version-warnings"
        )
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
}

dependencies {
    implementation(project(":mathc"))
    implementation(project(":colorutilsc"))
    implementation(project(":rboardcomponents"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.analytics.ktx)

    implementation(libs.protobuf.dynamic)

    implementation(libs.legacy.support.v4)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.commons.text)

    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.nio)
    implementation(libs.insetter)
    implementation(libs.core.ktx)

    implementation(libs.kotlin.stdlib)
    implementation(libs.core)

    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.preference.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.android.shell)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.gson)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.simple.item.decoration)
    implementation(libs.prDownloader)
    implementation(libs.balloon)
    implementation(libs.appcompat)
    implementation(libs.dots.indicator)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.browser)
    implementation(libs.glide)
    implementation(libs.flagkit)

    debugImplementation(libs.ui.tooling)
    implementation(libs.kotlin.reflect)

    implementation(libs.play.core)
    implementation(libs.play.core.ktx)

    ksp(libs.glide.ksp)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
}
