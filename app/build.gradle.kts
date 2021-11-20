import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    kotlin("kapt")
}

val libsuVersion = "3.1.2"
val kotlinVersion: String = project.getKotlinPluginVersion()

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"
    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthememanager"
        minSdk = 23
        targetSdk = 31
        versionCode = 342000
        versionName = "3.4.2"

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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:29.0.0"))
    implementation("com.google.firebase:firebase-messaging-ktx:23.0.0")
    implementation("com.google.firebase:firebase-analytics-ktx:20.0.0")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0-beta02")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0-beta02")
    implementation("org.apache.commons:commons-text:1.9")

    implementation("com.github.topjohnwu.libsu:core:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:io:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:busybox:$libsuVersion")
    implementation("dev.chrisbanes.insetter:insetter:0.6.1")
    implementation("androidx.core:core-ktx:1.7.0")
    //noinspection DifferentStdlibGradleVersion
    implementation(kotlin("stdlib-jdk7", kotlinVersion))
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.5.0-beta01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation("androidx.preference:preference-ktx:1.2.0-alpha02")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.0-rc01")
    implementation("com.jaredrummler:android-shell:1.0.0")
    implementation("com.google.firebase:firebase-analytics:20.0.0")
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.bignerdranch.android:simple-item-decoration:1.0.0")
    implementation("de.dertyp7214:PRDownloader:v0.6.0")
    implementation("com.github.skydoves:balloon:1.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4-alpha03")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha03")
    implementation("androidx.browser:browser:1.4.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")
    implementation("de.dertyp7214:PreferencesPlus:1.1")
    implementation("com.github.murgupluoglu:flagkit-android:1.0.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    debugImplementation("androidx.compose.ui:ui-tooling:1.1.0-beta02")
    implementation(kotlin("reflect", kotlinVersion))
}