plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization") version "2.2.0"
}

android {
    namespace = "com.algorand.android.credentials"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "PERA_AAGUID", "\"418a66da-f981-47e8-814f-19c97f97bd4d\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(project(":common-sdk"))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.dagger.hilt.android)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.fragment.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.kotlin.bip39)
    implementation(libs.logging.interceptor)
    implementation(files("../libs/dP256.jar"))

    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
}

room {
    schemaDirectory("$projectDir/schemas")
}
