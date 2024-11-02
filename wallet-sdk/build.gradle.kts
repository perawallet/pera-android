
/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("com.android.library")

    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.room)
}

apply(from = "../versions.gradle")

val targets = extra["targets"] as Map<*, *>

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_17}")
                }
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
    }

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach {
//        it.binaries.framework {
//            baseName = "common"
//            isStatic = true
//        }
//    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.kotlinfixture)
            implementation(libs.ktor.client.okhttp)
//            implementation(libs.ktor.client.android)
//            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.room.runtime)
            implementation(libs.ktor.client.core)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
//        iosMain.dependencies {
//            implementation(libs.ktor.client.darwin)
//        }
        commonTest.dependencies {
            implementation(project(":test-utils"))
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mockk)
        }
    }
}

android {
    namespace = "com.algorand.common"
    compileSdk = targets["compileSdkVersion"] as Int
    defaultConfig {
        minSdk = targets["minSdkVersion"] as Int
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    // add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    // add("kspIosX64", libs.androidx.room.compiler)
    // add("kspIosArm64", libs.androidx.room.compiler)
    // add("kspCommonMainMetadata", libs.androidx.room.compiler)
}
