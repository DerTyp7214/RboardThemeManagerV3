@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")

    alias(libs.plugins.ksp)
}

android {
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    ndkVersion = "28.1.13356709"
    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true
    buildFeatures.buildConfig = true

    namespace = "de.dertyp7214.rboardthememanager"

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthememanager"
        minSdk = 31
        targetSdk = 36
        versionCode = 395005
        versionName = "3.9.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    androidResources {
        localeFilters += listOf(
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
    kotlin {
        compilerOptions {
            freeCompilerArgs = freeCompilerArgs.get() + listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
                "-Xsuppress-version-warnings"
            )
            jvmToolchain(23)
            jvmTarget.set(JvmTarget.JVM_23)
        }
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
    lint {
        baseline = file("lint-baseline.xml")
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
