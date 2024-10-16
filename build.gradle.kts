// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.compose.compiler) apply false
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
