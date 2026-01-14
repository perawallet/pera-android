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

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.agp)
        classpath(libs.dagger.hilt.android.gradle.plugin)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.google.services)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.ksp.gradle.plugin)
        classpath(libs.navigation.safe.args.gradle.plugin)
        classpath(libs.firebase.perf.plugin)
        classpath(libs.kover.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.buildConfig).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.room).apply(false)
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

tasks.register("kover") {
    dependsOn(":common-sdk:koverHtmlReport")
    group = "verification"
    description = "Runs koverHtmlReport for the common-sdk module"
}

tasks.register("runGithubActionUnitTests") {
    group = "verification"
    description = "Runs specific unit tests for app, common-sdk, and credentials modules"

    dependsOn(":app:testProdDebugUnitTest")
    dependsOn(":common-sdk:testDebugUnitTest")
    dependsOn(":credentials:testDebugUnitTest")
}

subprojects {
    configurations.all {
        resolutionStrategy {
            force("org.bouncycastle:bcprov-jdk18on:1.82")
            force("org.bouncycastle:bcpkix-jdk18on:1.82")
            force("org.bouncycastle:bctls-jdk18on:1.82")

            eachDependency {
                if (requested.group == "org.bouncycastle" && requested.name == "bcprov-jdk15to18") {
                    useTarget("org.bouncycastle:bcprov-jdk18on:1.82")
                }
            }
        }
    }
}
