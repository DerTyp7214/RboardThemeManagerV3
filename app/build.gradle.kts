import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

//import com.google.protobuf.gradle.*

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    //id("com.google.protobuf")
    id("kotlin-android-extensions")
    kotlin("android")
    kotlin("kapt")
}

val libsuVersion = "3.2.1"
val kotlinVersion: String = project.getKotlinPluginVersion()

android {
    compileSdk = 32
    buildToolsVersion = "31.0.0"
    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthememanager"
        minSdk = 23
        targetSdk = 32
        versionCode = 344003
        versionName = "3.4.4"

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

/*protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.10.0"
    }

    plugins {
        id("javalite") {
            artifact = "com.google.protobuf:protoc-gen-javalite:3.0.0"
        }
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.25.0"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("javalite")
                id("grpc")
            }
        }
    }
}*/

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:29.0.4"))
    implementation("com.google.firebase:firebase-messaging-ktx:23.0.0")
    implementation("com.google.firebase:firebase-analytics-ktx:20.0.2")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0-rc01")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0-rc01")
    implementation("org.apache.commons:commons-text:1.9")

    implementation("com.github.topjohnwu.libsu:core:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:io:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:busybox:$libsuVersion")
    implementation("dev.chrisbanes.insetter:insetter:0.6.1")
    implementation("androidx.core:core-ktx:1.8.0-alpha02")
    //noinspection DifferentStdlibGradleVersion
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10")
    implementation("androidx.core:core:1.8.0-alpha02")
    implementation("com.google.android.material:material:1.6.0-alpha02")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.preference:preference-ktx:1.2.0-rc01")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.0")
    implementation("com.jaredrummler:android-shell:1.0.0")
    implementation("com.google.firebase:firebase-analytics:20.0.2")
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.bignerdranch.android:simple-item-decoration:1.0.0")
    implementation("de.dertyp7214:PRDownloader:v0.6.0")
    implementation("com.github.skydoves:balloon:1.4.2-SNAPSHOT")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4-alpha03")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha03")
    implementation("androidx.browser:browser:1.4.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")
    implementation("de.dertyp7214:PreferencesPlus:1.1")
    implementation("com.github.murgupluoglu:flagkit-android:1.0.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    debugImplementation("androidx.compose.ui:ui-tooling:1.2.0-alpha01")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")

    /*var grpc_version = "1.25.0"
    implementation("io.grpc:grpc-android:$grpc_version")
    implementation("io.grpc:grpc-okhttp:$grpc_version")
    implementation("io.grpc:grpc-protobuf-lite:$grpc_version")
    implementation("io.grpc:grpc-stub:$grpc_version")

    var javax_annotation_version = "1.3.2"
    implementation("javax.annotation:javax.annotation-api:$javax_annotation_version")

    implementation("com.google.protobuf:protobuf-kotlin:3.19.3")*/
}