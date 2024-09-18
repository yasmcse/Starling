

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.dagger.hilt)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    val libs = catalogs.named("libs")

    namespace = "com.challenge.starlingbank"
    compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()

    defaultConfig {
        applicationId = libs.findVersion("applicationId").get().requiredVersion
        minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
        targetSdk = libs.findVersion("targetSdk").get().requiredVersion.toInt()
        versionCode = libs.findVersion("appVersionCode").get().requiredVersion.toInt()
        versionName = libs.findVersion("appVersionName").get().requiredVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    packaging {
        resources {
            excludes += "/META-INF/gradle/*"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.findVersion("kotlinCompilerExtVersion").get().requiredVersion
    }

}

dependencies {

    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:home"))
    implementation(project(":feature:savingsgoals"))
    implementation(project(":core:common"))
    implementation(project(":domain:repositorycontract"))

    // Hilt
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hilt.compiler)

    implementation(libs.bundles.activity.material.bundle)
    implementation(libs.bundles.compose.bundle)
    implementation(libs.bundles.navigation.bundle)
}