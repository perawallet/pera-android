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

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
    }

    packaging {
        resources {
            excludes.apply {
                add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
                add("META-INF/DEPENDENCIES.md")
                add("META-INF/NOTICE.md")
                add("META-INF/LICENSE.md")
                add("META-INF/LICENSE.txt")
                add("META-INF/LICENSE-notice.md")
                add("META-INF/NOTICE.txt")
                add("META-INF/ASL2.0.md")
            }
        }
    }
}

dependencies {

    api(libs.algosdk)
    api(libs.algorand.go.mobile)

    implementation("net.java.dev.jna:jna:5.14.0@aar")
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
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.lifecycle.runtime.testing)
    testImplementation(libs.mockito)
    testImplementation(libs.mockk)
    testImplementation(project(":test-utils"))
}

room {
    schemaDirectory("$projectDir/schemas")
}
