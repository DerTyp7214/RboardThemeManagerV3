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

val libsuVersion = "5.0.1"
val kotlinVersion: String = project.getKotlinPluginVersion()

android {
    compileSdkPreview = "Tiramisu"
    buildToolsVersion = "33.0.0-rc4"
    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true

    defaultConfig {
        applicationId = "de.dertyp7214.rboardthememanager"
        minSdk = 23
        targetSdk = 32
        versionCode = 350000
        versionName = "3.5.0"

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
        resources.excludes.add("META-INF/*")
    }
    namespace = "de.dertyp7214.rboardthememanager"
}

/*protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.0.0-rc-2"
    }

    plugins {
        id("javalite") {
            artifact = "com.google.protobuf:protoc-gen-javalite:3.0.0"
        }
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.45.0"
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
    implementation(platform("com.google.firebase:firebase-bom:30.0.0"))
    implementation("com.google.firebase:firebase-messaging-ktx:23.0.5")
    implementation("com.google.firebase:firebase-analytics-ktx:21.0.0")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0-rc01")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0-rc01")
    implementation("org.apache.commons:commons-text:1.9")

    implementation("com.github.topjohnwu.libsu:core:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:io:$libsuVersion")
    implementation("com.github.topjohnwu.libsu:nio:$libsuVersion")
    implementation("dev.chrisbanes.insetter:insetter:0.6.1")
    implementation("androidx.core:core-ktx:1.9.0-alpha04")
    //noinspection DifferentStdlibGradleVersion
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.0-Beta")
    implementation("androidx.core:core:1.9.0-alpha04")
    implementation("com.google.android.material:material:1.7.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.activity:activity-ktx:1.6.0-alpha04")
    implementation("androidx.fragment:fragment-ktx:1.5.0-rc01")
    implementation("com.jaredrummler:android-shell:1.0.0")
    implementation("com.google.firebase:firebase-analytics:21.0.0")
    implementation("com.google.firebase:firebase-messaging:23.0.5")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.bignerdranch.android:simple-item-decoration:1.0.0")
    implementation("de.dertyp7214:PRDownloader:v0.6.0")
    implementation("com.github.skydoves:balloon:1.4.6-SNAPSHOT")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4-alpha06")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha06")
    implementation("androidx.browser:browser:1.4.0")
    implementation("com.github.bumptech.glide:glide:4.13.2")
    kapt("com.github.bumptech.glide:compiler:4.13.2")
    implementation("de.dertyp7214:PreferencesPlus:1.1")
    implementation("com.github.murgupluoglu:flagkit-android:1.0.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    debugImplementation("androidx.compose.ui:ui-tooling:1.2.0-beta02")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.0-Beta")

    /*var grpc_version = "1.45.0"
    implementation("io.grpc:grpc-android:$grpc_version")
    implementation("io.grpc:grpc-okhttp:$grpc_version")
    implementation("io.grpc:grpc-protobuf-lite:$grpc_version")
    implementation("io.grpc:grpc-stub:$grpc_version")

    var javax_annotation_version = "1.3.2"
    implementation("javax.annotation:javax.annotation-api:$javax_annotation_version")

    implementation("com.google.protobuf:protobuf-kotlin:3.20.0-rc-1")*/
}