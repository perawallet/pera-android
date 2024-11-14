// Top-level build file where you can add configuration options common to all sub-projects/modules.

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
        classpath(libs.perf.plugin)

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.multiplatform).apply(false)
}

apply(from = "versions.gradle")

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.bouncycastle:bcprov-jdk15to18"))
                .using(module("org.bouncycastle:bcprov-jdk18on:1.77"))
            substitute(module("org.bouncycastle:bcprov-jdk15on"))
                .using(module("org.bouncycastle:bcprov-jdk18on:1.77"))
            substitute(module("org.bouncycastle:bcprov-jdk18on"))
                .using(module("org.bouncycastle:bcprov-jdk18on:1.77"))

            substitute(module("org.bouncycastle:bcutil-jdk15to18"))
                .using(module("org.bouncycastle:bcutil-jdk18on:1.77"))
            substitute(module("org.bouncycastle:bcutil-jdk15on"))
                .using(module("org.bouncycastle:bcutil-jdk18on:1.77"))
            substitute(module("org.bouncycastle:bcutil-jdk18on"))
                .using(module("org.bouncycastle:bcutil-jdk18on:1.77"))
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
