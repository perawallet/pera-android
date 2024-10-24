plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

apply(from = "../versions.gradle")

val targets = extra["targets"] as Map<*, *>

android {
    namespace = "com.algorand.test"
    compileSdk = targets["compileSdkVersion"] as Int

    defaultConfig {
        minSdk = targets["minSdkVersion"] as Int

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinfixture)
    implementation(libs.junit)
    implementation(libs.mockito)
}
