/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.compose)
    alias(libs.plugins.detekt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room.module)
    alias(libs.plugins.ksp)
}

apply(from = "$projectDir/quality.gradle")

listOf("flavors.gradle.kts").forEach { fileName ->
    if (project.file(fileName).exists()) apply(from = fileName)
}

val getVersionName = {
    System.getenv("PERA_APP_VERSION_NAME") ?: libs.versions.android.versionName.get()
}

val getVersionCode = {
    System.getenv("PERA_APP_VERSION_CODE")?.toInt()
        ?: libs.versions.android.versionCode.get().toInt()
}

val gitHashProvider = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.map { it.trim() }.orElse("unknown")

android {
    namespace = libs.versions.android.namespace.get()
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    ndkVersion = libs.versions.ndk.get()

    defaultConfig {
        // Offramp API keys
        val offrampApiKeyProps = Properties()
        val offrampPropsFile = rootProject.file("app/offramp-api-key.properties")
        if (offrampPropsFile.exists()) {
            FileInputStream(offrampPropsFile).use { offrampApiKeyProps.load(it) }
        }
        buildConfigField(
            "String",
            "PROD_BIDALI_API_KEY",
            "${offrampApiKeyProps.getProperty("PROD_BIDALI_API_KEY", "")}"
        )
        buildConfigField(
            "String",
            "STAGING_BIDALI_API_KEY",
            "${offrampApiKeyProps.getProperty("STAGING_BIDALI_API_KEY", "")}"
        )

        // Browser packages
        val browserPackageProps = Properties()
        val browserPropsFile = project.file("browser-package.properties")
        if (browserPropsFile.exists()) {
            FileInputStream(browserPropsFile).use { browserPackageProps.load(it) }
        }
        for ((key, value) in browserPackageProps) {
            val k = key.toString()
            val v = value.toString()
            buildConfigField("String", k, "\"$v\"")
            manifestPlaceholders[k] = v
        }

        applicationId = libs.versions.android.namespace.get()
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        versionCode = getVersionCode()
        versionName = getVersionName()

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Release builds support all architectures, debug builds only arm64-v8a for faster compilation
        ndk { abiFilters += listOf("arm64-v8a") }

        // BuildConfig fields
        buildConfigField("String", "GitHash", "\"${gitHashProvider.get()}\"")
        buildConfigField("String", "APPLICATION_NAME", "\"pera\"")
        buildConfigField("String", "DEEPLINK_PREFIX", "\"algorand://\"")
        buildConfigField("String", "PERA_WC_DEEPLINK_PREFIX", "\"perawallet-wc://\"")
        buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
        buildConfigField("Integer", "VERSION_CODE", versionCode.toString())
    }

    signingConfigs {
        create("releaseLocal") {
            val propsFile = file("../local.properties")
            if (!propsFile.exists()) return@create

            val props = Properties().apply {
                load(FileInputStream(propsFile))
            }
            try {
                storeFile = file(props["RELEASE_STORE_FILE"] ?: error("Missing RELEASE_STORE_FILE"))
                storePassword = props["RELEASE_STORE_PASSWORD"]?.toString()
                keyAlias = props["RELEASE_KEY_ALIAS"]?.toString()
                keyPassword = props["RELEASE_KEY_PASSWORD"]?.toString()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    buildTypes {

        getByName("release") {
            isMinifyEnabled = true
            multiDexEnabled = true
            // signingConfig = signingConfigs.getByName("releaseLocal") //enable this to sign the release

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            manifestPlaceholders["enableCrashReporting"] = "true"
            manifestPlaceholders["enableFirebasePerformanceLogcat"] = "false"

            // Release builds support all architectures
            ndk { abiFilters.clear(); abiFilters += listOf("armeabi-v7a", "x86", "x86_64", "arm64-v8a") }
        }

        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            multiDexEnabled = true
            applicationIdSuffix = ".debug"

            manifestPlaceholders["enableCrashReporting"] = "false"
            manifestPlaceholders["enableFirebasePerformanceLogcat"] = "false"

            resValue("string", "app_name", "Pera (Dev)")

            // Debug build optimizations
            // Only build arm64-v8a for faster debug builds (already set in defaultConfig)
            // Disable PNG crunching for faster builds
            isCrunchPngs = false
        }
    }

    flavorDimensions += "server"

    productFlavors {

        val apiKeyProps = Properties()
        val apiUrlProps = Properties()
        val arc59Props = Properties()

        fun load(filePath: String, into: Properties) =
            into.load(FileInputStream(file(filePath)))

        create("staging") {
            dimension = "server"
            applicationIdSuffix = ".staging"

            load("../app/src/staging/api-key.properties", apiKeyProps)
            load("../app/src/main/api-url.properties", apiUrlProps)
            load("../app/arc59.properties", arc59Props)

            buildConfigField("String", "ALGORAND_BASE_URL", apiUrlProps["NODE_TESTNET_URL"].toString())
            buildConfigField("String", "ALGORAND_API_KEY", apiKeyProps["ALGORAND_API_KEY"].toString())
            buildConfigField("String", "INDEXER_API_KEY", apiKeyProps["INDEXER_API_KEY"].toString())
            buildConfigField("String", "MOBILE_API_KEY", apiKeyProps["MOBILE_API_KEY"].toString())

            buildConfigField(
                "String",
                "MOBILE_ALGORAND_MAINNET_BASE_URL",
                "\"https://mainnet.staging.api.perawallet.app/\""
            )
            buildConfigField(
                "String",
                "MOBILE_ALGORAND_TESTNET_BASE_URL",
                "\"https://testnet.staging.api.perawallet.app/\""
            )

            buildConfigField(
                "String",
                "PERA_3D_EXPLORER_BASE_URL",
                "\"https://static-hosting.perawallet.app/3dviewer/index.html\""
            )

            buildConfigField("String", "DISCOVER_WEBVIEW_USERNAME", apiKeyProps["DISCOVER_WEBVIEW_USERNAME"].toString())
            buildConfigField("String", "DISCOVER_WEBVIEW_PASSWORD", apiKeyProps["DISCOVER_WEBVIEW_PASSWORD"].toString())

            buildConfigField(
                "String",
                "DISCOVER_VERSION",
                "\"${libs.versions.android.discover.staging.get().toInt()}\""
            )

            buildConfigField("String", "NODE_MAINNET_URL", apiUrlProps["NODE_MAINNET_URL"].toString())
            buildConfigField("String", "NODE_TESTNET_URL", apiUrlProps["NODE_TESTNET_URL"].toString())
            buildConfigField("String", "INDEXER_MAINNET_URL", apiUrlProps["INDEXER_MAINNET_URL"].toString())
            buildConfigField("String", "INDEXER_TESTNET_URL", apiUrlProps["INDEXER_TESTNET_URL"].toString())

            buildConfigField("String", "ARC59_APP_ADDR_TESTNET", arc59Props["ARC59_APP_ADDR_TESTNET"].toString())
            buildConfigField("long", "ARC59_APP_ID_TESTNET", arc59Props["ARC59_APP_ID_TESTNET"].toString())
            buildConfigField("String", "ARC59_APP_ADDR_MAINNET", arc59Props["ARC59_APP_ADDR_MAINNET"].toString())
            buildConfigField("long", "ARC59_APP_ID_MAINNET", arc59Props["ARC59_APP_ID_MAINNET"].toString())

            buildConfigField("String", "DISCOVER_URL", "\"https://discover-mobile-staging.perawallet.app\"")
            buildConfigField("String", "STAKING_URL", "\"https://staking-mobile-staging.perawallet.app\"")

            buildConfigField("String", "MELD_TESTNET_URL", "\"https://testnet.api.perawallet.app\"")
            buildConfigField("String", "CARDS_TESTNET_URL", "\"https://cards-mobile-staging-testnet.perawallet.app\"")

            buildConfigField("String", "MELD_MAINNET_URL", "\"https://mainnet.staging.api.perawallet.app\"")
            buildConfigField("String", "CARDS_MAINNET_URL", "\"https://cards-mobile-staging-mainnet.perawallet.app\"")

            buildConfigField("String", "ONRAMP_URL", "\"https://onramp-mobile-staging.perawallet.app\"")
        }

        create("prod") {
            dimension = "server"

            load("../app/src/prod/api-key.properties", apiKeyProps)
            load("../app/src/main/api-url.properties", apiUrlProps)
            load("../app/arc59.properties", arc59Props)

            buildConfigField("String", "ALGORAND_BASE_URL", apiUrlProps["NODE_MAINNET_URL"].toString())
            buildConfigField("String", "ALGORAND_API_KEY", apiKeyProps["ALGORAND_API_KEY"].toString())
            buildConfigField("String", "INDEXER_API_KEY", apiKeyProps["INDEXER_API_KEY"].toString())
            buildConfigField("String", "MOBILE_API_KEY", apiKeyProps["MOBILE_API_KEY"].toString())

            buildConfigField("String", "MOBILE_ALGORAND_MAINNET_BASE_URL", "\"https://mainnet.api.perawallet.app/\"")
            buildConfigField("String", "MOBILE_ALGORAND_TESTNET_BASE_URL", "\"https://testnet.api.perawallet.app/\"")

            buildConfigField(
                "String",
                "PERA_3D_EXPLORER_BASE_URL",
                "\"https://static-hosting.perawallet.app/3dviewer/index.html\""
            )

            buildConfigField("String", "DISCOVER_WEBVIEW_USERNAME", apiKeyProps["DISCOVER_WEBVIEW_USERNAME"].toString())
            buildConfigField("String", "DISCOVER_WEBVIEW_PASSWORD", apiKeyProps["DISCOVER_WEBVIEW_PASSWORD"].toString())

            buildConfigField(
                "String",
                "DISCOVER_VERSION",
                "\"${libs.versions.android.discover.prod.get().toInt()}\""
            )

            buildConfigField("String", "NODE_MAINNET_URL", apiUrlProps["NODE_MAINNET_URL"].toString())
            buildConfigField("String", "NODE_TESTNET_URL", apiUrlProps["NODE_TESTNET_URL"].toString())
            buildConfigField("String", "INDEXER_MAINNET_URL", apiUrlProps["INDEXER_MAINNET_URL"].toString())
            buildConfigField("String", "INDEXER_TESTNET_URL", apiUrlProps["INDEXER_TESTNET_URL"].toString())

            buildConfigField("String", "ARC59_APP_ADDR_TESTNET", arc59Props["ARC59_APP_ADDR_TESTNET"].toString())
            buildConfigField("long", "ARC59_APP_ID_TESTNET", arc59Props["ARC59_APP_ID_TESTNET"].toString())
            buildConfigField("String", "ARC59_APP_ADDR_MAINNET", arc59Props["ARC59_APP_ADDR_MAINNET"].toString())
            buildConfigField("long", "ARC59_APP_ID_MAINNET", arc59Props["ARC59_APP_ID_MAINNET"].toString())

            buildConfigField("String", "DISCOVER_URL", "\"https://discover-mobile.perawallet.app\"")
            buildConfigField("String", "STAKING_URL", "\"https://staking-mobile.perawallet.app\"")

            buildConfigField("String", "MELD_TESTNET_URL", "\"https://testnet.api.perawallet.app\"")
            buildConfigField("String", "CARDS_TESTNET_URL", "\"https://cards-mobile.perawallet.app\"")

            buildConfigField("String", "MELD_MAINNET_URL", "\"https://mainnet.api.perawallet.app\"")
            buildConfigField("String", "CARDS_MAINNET_URL", "\"https://cards-mobile.perawallet.app\"")

            buildConfigField("String", "ONRAMP_URL", "\"https://onramp-mobile.perawallet.app\"")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(libs.versions.jvm.get().toInt())
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    packaging.resources.excludes += listOf(
        "META-INF/io.netty.versions.properties",
        "META-INF/INDEX.LIST",
        "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
        "META-INF/DEPENDENCIES.md",
        "META-INF/NOTICE.md",
        "META-INF/LICENSE.md",
        "META-INF/LICENSE.txt",
        "META-INF/NOTICE.txt",
        "META-INF/ASL2.0.md",

        "mac/**", "darwin/**", "osx/**",
        "linux/**", "windows/**", "windows64/**",

        "lib/**/libsodium.dylib",
        "lib/**/libsodium.dll",
        "lib/libnarcissus-macos-64.dylib",
        "lib/libnarcissus-win-32.dll",
        "lib/libnarcissus-win-64.dll"
    )
}

room {
    schemaDirectory("$projectDir/schemas")
}

ksp {
    arg("room.verifySchema", "false")
}

// Disable Firebase Performance instrumentation for debug builds (significant build time savings)
android.buildTypes.all {
    val isDebugBuild = name == "debug"
    configure<com.google.firebase.perf.plugin.FirebasePerfExtension> {
        setInstrumentationEnabled(!isDebugBuild)
    }
}

dependencies {

    // Internal modules
    implementation(project(":common-sdk"))
    implementation(project(":credentials"))

    // Kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.lifecycle.process)
    implementation(libs.lifecycle.extensions)
    implementation(libs.lifecycle.common.java8)

    // MultiDex
    implementation(libs.multidex)

    // UI / Compose
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.legacy.support.v4)
    implementation(libs.flexbox)
    implementation(libs.browser)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.fragment)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.constraintlayout)

    implementation(libs.lottie)
    implementation(libs.lottie.compose)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // DI: Hilt + Koin
    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.compose.navigation)
    implementation(libs.androidx.compose.foundation.layout)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.core)

    implementation(libs.java.websocket)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Room / Storage
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    coreLibraryDesugaring(libs.desugar.jdk)

    // Crypto / Wallet
    implementation(libs.algorand.go.mobile)
    implementation(libs.wallet.connect.v1)
    implementation(libs.wallet.connect.v2)
    implementation(libs.pera.compact.decimal.format)
    implementation(libs.kotlin.bip39)
    implementation(libs.ble)
    implementation(libs.androidx.credentials)
    implementation(libs.tink.android)
    implementation(libs.biometric)

    // Media
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // Tools
    implementation(libs.zxing.android.embedded)
    implementation(libs.glide.image)
    implementation(libs.glide.compose)
    ksp(libs.glide.compiler)

    implementation(libs.paging.runtime.ktx)
    implementation(libs.paging.compose)
    implementation(libs.review.ktx)
    implementation(libs.installreferrer)

    implementation(libs.mpandroidchart)

    // Testing
    testImplementation(project(":test-utils"))
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito)

    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.junit.ktx)
}
