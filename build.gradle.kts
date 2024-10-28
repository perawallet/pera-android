// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.agp)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.navigation.safe.args.gradle.plugin)
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.perf.plugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

apply(from = "versions.gradle")

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
