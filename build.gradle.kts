import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.github.ben-manes.versions") version "0.52.0" //  ./gradlew dependencyUpdates
}

buildscript{
    dependencies {
//        classpath("com.google.gms:google-services:4.4.1")
    }
}


ext {
    set("compileSdk", 35)
    set("minSdk", 23)
    set("applicationId", "com.xueh.commonlib")
    set("appName", "commonlib")
    set("targetSdk", 35)
    set("versionCode", 1)
    set("versionName", "1.0.0")
}


//allprojects {
//    apply<com.github.benmanes.gradle.versions.VersionsPlugin>()
//
//    tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
//        // 设定废弃策略，默认为“release”，可设定为“alpha”、“beta” 或“milestone”
//        rejectVersionIf {
//            isStable(candidate.version) && !isStable(currentVersion)
//        }
//    }
//}

// 例如，允许 beta 版本
fun isStable(version: String): Boolean {
    // 自定义稳定版本的判断逻辑
    return !version.contains("alpha") && !version.contains("beta") && !version.contains("RC")
}