import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.ksp)
}

android {
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    buildFeatures.dataBinding = true
    ndkVersion = "28.2.13676358"
    buildFeatures.viewBinding = true
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthememanager"
        minSdk = 23
        targetSdk = 36
        versionCode = 396000
        versionName = "3.9.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    @Suppress("UnstableApiUsage") androidResources {
        localeFilters += listOf(
            "ar", "cs", "da", "de",
            "el", "en", "es", "fi",
            "fr", "hi", "hu", "in",
            "it", "ja", "nl", "no",
            "pl", "pt-rBR", "ro", "ru",
            "sv", "uk", "vi", "zh-rCN",
            "zh-rTW"
        )
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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

    namespace = "de.dertyp7214.rboardthememanager"
}

dependencies {
    implementation(project(":colorutilsc"))
    implementation(project(":mathc"))
    implementation(project(":rboardcomponents"))
    implementation(platform(libs.firebase.bom))

    implementation(libs.protobuf.dynamic)

    implementation(libs.legacy.support.v4)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.commons.text)

    implementation(libs.core)
    implementation(libs.io)
    implementation(libs.nio)
    implementation(libs.insetter)
    implementation(libs.core.ktx)
    implementation(libs.dots.indicator)
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.preference.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.android.shell)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.gson)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.simple.item.decoration)
    implementation(libs.prdownloader)
    implementation(libs.balloon)
    implementation(libs.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.androidx.browser)
    implementation(libs.glide)
    ksp(libs.ksp)
    implementation(libs.flagkit.android)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.kotlin.reflect)
    coreLibraryDesugaring(libs.desugar.jdk.libs.nio)
}
