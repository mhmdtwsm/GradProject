import org.gradle.api.JavaVersion

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {

    namespace = "com.example.project1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.project1"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        packaging {
            resources {
                excludes += "META-INF/INDEX.LIST"
                excludes += "META-INF/DEPENDENCIES"
                excludes += "META-INF/io.netty.versions.properties"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }


}

dependencies {
    // Retrofit for network calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // Moshi for JSON parsing
    implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    implementation("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")

    //  Splash Screen
    implementation(libs.androidx.core.splashscreen)
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("com.google.accompanist:accompanist-insets:0.30.1")

    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.material:material:1.4.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.4")

    // Request Handling
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.json:json:20210307")

    // Image Coil Library
    implementation("io.coil-kt:coil-compose:2.4.0")

    // kotlinx.serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // Annotation processor for kotlinx.serialization
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.8.21")


    // Camera X dependencies

    implementation("androidx.camera:camera-core:1.2.3")
    implementation("androidx.camera:camera-camera2:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("androidx.camera:camera-view:1.2.3")

    // ML Kit for barcode scanning
    implementation("com.google.mlkit:barcode-scanning:17.1.0")

    // For permissions handling
    implementation("androidx.activity:activity-compose:1.7.2")

    // Add Guava to resolve ListenableFuture conflicts
    implementation("com.google.guava:guava:31.1-android")

    // Add multidex support
    implementation("androidx.multidex:multidex:2.0.1")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.volley)
    implementation(libs.firebase.appdistribution.gradle)
    implementation(libs.play.services.vision)
    implementation(libs.barcode.scanning.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}