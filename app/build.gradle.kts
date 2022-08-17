@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
}

val libsuVersion = "5.0.2"
val kotlinVersion: String = project.getKotlinPluginVersion()

@Suppress("UnstableApiUsage")
android {
    compileSdk = 33
    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthememanager"
        minSdk = 31
        targetSdk = 33
        versionCode = 360000
        versionName = "3.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        create("pro") {
            initWith(getByName("release"))
            applicationIdSuffix = ".pro"
            matchingFallbacks += listOf("")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_15
        targetCompatibility = JavaVersion.VERSION_15
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_15.toString()
    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(project(":mathc"))
    implementation(project(":colorutilsc"))
    implementation(platform("com.google.firebase:firebase-bom:30.1.0"))
    implementation("com.google.firebase:firebase-messaging-ktx:23.0.7")
    implementation("com.google.firebase:firebase-analytics-ktx:21.1.0")

    implementation("com.github.DerTyp7214:PreferencesPlus:1.0")

    implementation("com.google.protobuf:protobuf-kotlin:3.21.5")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")
    implementation("org.apache.commons:commons-text:1.9")

    implementation("com.github.topjohnwu.libsu:core:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:io:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:nio:$libsuVersion")
    implementation("dev.chrisbanes.insetter:insetter:0.6.1")
    implementation("androidx.core:core-ktx:1.9.0-beta01")
    //noinspection DifferentStdlibGradleVersion

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.10")
    implementation("androidx.core:core:1.9.0-beta01")

    implementation("com.google.android.material:material:1.7.0-beta01")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha03")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.activity:activity-ktx:1.6.0-beta01")
    implementation("androidx.fragment:fragment-ktx:1.5.2")
    implementation("com.jaredrummler:android-shell:1.0.0")
    implementation("com.google.firebase:firebase-analytics:21.1.0")
    implementation("com.google.firebase:firebase-messaging:23.0.7")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0-alpha01")
    implementation("com.bignerdranch.android:simple-item-decoration:1.0.0")
    implementation("de.dertyp7214:PRDownloader:v0.6.0")
    implementation("com.github.skydoves:balloon:1.4.5")
    implementation("androidx.appcompat:appcompat:1.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4-alpha07")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha07")
    implementation("androidx.browser:browser:1.4.0")
    implementation("com.github.bumptech.glide:glide:4.13.2")
    kapt("com.github.bumptech.glide:compiler:4.13.2")
    implementation("com.github.murgupluoglu:flagkit-android:1.0.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    debugImplementation("androidx.compose.ui:ui-tooling:1.3.0-alpha03")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

    implementation("com.google.android.play:core:1.10.3")
    implementation("com.google.android.play:core-ktx:1.8.1")
}