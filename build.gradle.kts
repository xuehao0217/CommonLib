// Top-level build file where you can add configuration options common to all sub-projects/modules.
// AGP 9：内置 Kotlin，勿再应用 org.jetbrains.kotlin.android；详见 docs/AGP9_MIGRATION.md

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.parcelize) apply false
    // 应用在根工程，供 ./gradlew dependencyUpdates 使用（勿加 apply false）
    alias(libs.plugins.ben.manes.versions)
}
