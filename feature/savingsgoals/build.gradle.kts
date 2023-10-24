@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.dagger.hilt)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.challenge.savingsgoals"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

//    packaging {
//        resources {
//            excludes += "/META-INF/gradle/incremental.annotation.processors"
//        }
//    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }

    // Allow references to generated code
    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))

    // Hilt
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hilt.compiler)

    // Activity-Fragment-Material-compose material bundle
    implementation(libs.bundles.activity.fragment.material.bundle)
    // compose bundle
    implementation(libs.bundles.compose.bundle)
    // Jetpack Navigation Bundle
    implementation(libs.bundles.navigation.bundle)
    // Life Cycle Bundle
    implementation(libs.bundles.lifecycle.bundle)

    // Unit Test bundle
    testImplementation(libs.bundles.unit.test.bundle)
}