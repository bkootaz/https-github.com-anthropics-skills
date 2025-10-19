plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.autoai.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.autoai.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Icons
    implementation("androidx.compose.material:material-icons-extended")

    // WorkManager for crawling/sync
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Networking and HTML parsing
    implementation("io.ktor:ktor-client-okhttp:2.3.10")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")
    implementation("org.jsoup:jsoup:1.18.1")

    // SQLite/Room for local cache
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // DataStore for settings
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Compose ViewModel helpers
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // ONNX Runtime Mobile for embeddings
    implementation("com.microsoft.onnxruntime:onnxruntime-mobile:1.20.1")

    // llama.cpp via ggml-android bindings (placeholder - manual AAR if needed)
    // implementation(files("libs/llama-android.aar"))
}
