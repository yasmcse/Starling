plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.dagger.hilt)
    kotlin("kapt")
}

android {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    val libs = catalogs.named("libs")

    namespace = "com.challenge.starlingbank.networklayer"
    compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()

    defaultConfig {
        minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    packaging {
        resources {
            excludes += "/META-INF/*"
        }
    }
    android.buildFeatures.buildConfig = true

    buildTypes {
        debug {
            buildConfigField(
                "String", "accessToken",
                com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir)
                    .getProperty("ACCESS_TOKEN")
            )

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }


        release {
            buildConfigField(
                "String", "accessToken",
                com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir)
                    .getProperty("ACCESS_TOKEN")
            )

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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.findVersion("kotlinCompilerExtVersion").get().requiredVersion
    }
}

dependencies {

   // implementation(project(":domain:repositorycontract"))
    implementation(project(":core:common"))
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.bundles.retrofit.bundle)
    // Unit Test bundle
    testImplementation(libs.bundles.unit.test.bundle)
}