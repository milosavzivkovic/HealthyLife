plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "rs.healthylife.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "rs.healthylife.app"
        minSdk = 26 // Android 8.0 i noviji
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Googlov gotov ekran za skeniranje bar-koda (ML Kit, radi i bez interneta)
    implementation(libs.play.services.code.scanner)

    // Open Food Facts API: HTTP zahtevi i citanje JSON-a
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)

    // Slike proizvoda
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
