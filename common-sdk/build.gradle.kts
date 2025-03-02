plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

apply(from = "./test-coverage/kover.gradle")

android {
    namespace = "com.algorand.wallet"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {

    api(libs.algosdk)
    api(libs.algorand.go.mobile)

    implementation(libs.xhdwalletapi)
    implementation(libs.kotlin.bip39)
    implementation(libs.dagger.hilt.android)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.crashlytics)
    implementation(project.dependencies.platform(libs.firebase.bom))

    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.mockito)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(project(":test-utils"))

    androidTestImplementation(libs.espresso.core)
}

room {
    schemaDirectory("$projectDir/schemas")
}
