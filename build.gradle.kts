// Top-level build file where you can add configuration options common to all sub-projects/modules.
//import buildsrc.*
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
}



ext {
    set("compileSdk", 34)
    set("minSdk", 23)
    set("applicationId", "com.xueh.commonlib")
    set("appName", "commonlib")
    set("targetSdk", 34)
    set("versionCode", 1)
    set("versionName", "1.0.0")
    set("compose_compiler", "1.5.1")
}
