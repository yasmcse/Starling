@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.dagger.hilt)
    kotlin("kapt")
    alias(libs.plugins.kotlin.parcelize)

}

android {
    namespace = "com.challenge.common"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    packaging {
        resources {
            excludes += "/META-INF/*"
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
    }
    buildFeatures {
        compose = true
    }

    composeOptions{
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // Hilt
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hilt.compiler)

    implementation(libs.bundles.gson)
    // Activity-Material-compose material bundle
    implementation(libs.bundles.activity.material.bundle)
    // compose bundle
    implementation(libs.bundles.compose.bundle)
    // Jetpack Navigation Bundle
    implementation(libs.bundles.navigation.bundle)
    // Life Cycle Bundle
    implementation(libs.bundles.lifecycle.bundle)
    // unit test bundle
    testImplementation(libs.bundles.unit.test.bundle)
}