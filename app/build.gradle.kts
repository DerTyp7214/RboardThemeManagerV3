import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    kotlin("kapt")
    id("com.google.devtools.ksp") version("1.9.0-Beta-1.0.11")
}

val libsuVersion = "5.1.0"
val kotlinVersion: String = project.getKotlinPluginVersion()

android {
    compileSdkPreview = "UpsideDownCake"
    buildToolsVersion = "34.0.0 rc4"
    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthememanager"
        minSdk = 23
        targetSdk = 33
        versionCode = 382000
        versionName = "3.8.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations += listOf(
            "af", "cs", "da", "de",
            "el", "en", "es", "fi",
            "fr", "hi", "hu", "id",
            "it", "ja", "nl", "no",
            "pl", "pt", "ro", "ru",
            "sv", "uk", "vi", "zh-rCN",
            "zh-rTW"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.description
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JvmTarget.JVM_17.description
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources.excludes.add("META-INF/*")
    }
    namespace = "de.dertyp7214.rboardthememanager"
}

dependencies {
    implementation(project(":colorutilsc"))
    implementation(project(":mathc"))
    implementation(project(":rboardcomponents"))

    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.2")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")

    implementation("com.github.DerTyp7214:PreferencesPlus:1.0")

    implementation("com.github.os72:protobuf-dynamic:1.0.1")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0-alpha01")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0-alpha01")
    implementation("org.apache.commons:commons-text:1.10.0")

    implementation("com.github.topjohnwu.libsu:core:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:io:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:nio:$libsuVersion")
    implementation("dev.chrisbanes.insetter:insetter:0.6.1")
    implementation("androidx.core:core-ktx:1.12.0-alpha04")
    //noinspection DifferentStdlibGradleVersion
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0-Beta")
    implementation("androidx.core:core:1.12.0-alpha04")
    implementation("com.google.android.material:material:1.10.0-alpha03")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha10")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.activity:activity-ktx:1.8.0-alpha04")
    implementation("androidx.fragment:fragment-ktx:1.6.0-rc01")
    implementation("com.jaredrummler:android-shell:1.0.0")
    implementation("com.google.firebase:firebase-analytics:21.3.0")
    implementation("com.google.firebase:firebase-messaging:23.1.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("com.bignerdranch.android:simple-item-decoration:1.0.0")
    implementation("de.dertyp7214:PRDownloader:v0.6.0")
    implementation("com.github.skydoves:balloon:1.5.4-SNAPSHOT")
    implementation("androidx.appcompat:appcompat:1.7.0-alpha02")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.0-alpha01")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0-alpha01")
    implementation("androidx.browser:browser:1.6.0-alpha01")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    ksp("com.github.bumptech.glide:ksp:4.15.1")
    implementation("com.github.murgupluoglu:flagkit-android:1.0.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0-beta01")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0-Beta")
}
