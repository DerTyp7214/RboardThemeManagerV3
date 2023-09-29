import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.ksp)
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    buildFeatures.dataBinding = true

    buildFeatures.viewBinding = true
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthememanager"
        // Update the minSdk if old Android Versions are no longer supported on the Gboard side.
        minSdk = 23
        targetSdk = 34
        versionCode = 385005
        versionName = "3.8.5"

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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }

    kotlinOptions {
        jvmTarget = JvmTarget.JVM_20.description
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JvmTarget.JVM_20.description
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
    namespace = "de.dertyp7214.rboardthememanager"
}

dependencies {
    implementation(project(":colorutilsc"))
    implementation(project(":mathc"))
    implementation(project(":rboardcomponents"))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.analytics.ktx)

    implementation(libs.preferencesplus)

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
