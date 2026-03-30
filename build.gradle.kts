// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.parcelize) apply false
    id("com.github.ben-manes.versions") version "0.53.0" //  ./gradlew dependencyUpdates
}
