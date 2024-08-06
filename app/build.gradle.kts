import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.xueh.commonlib"
    compileSdk = rootProject.extra["compileSdk"] as Int
    defaultConfig {
        applicationId = rootProject.extra["applicationId"] as String
        minSdk =rootProject.extra["minSdk"] as Int
        targetSdk = rootProject.extra["targetSdk"] as Int

        versionCode =rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters.addAll(arrayOf( "arm64-v8a"))
        }
    }
    //签名配置  https://blog.csdn.net/kongqwesd12/article/details/133313123
    // https://blog.csdn.net/jdsjlzx/article/details/136030728
//    signingConfigs {
//        create("release") {
//            keyAlias = "keyAlias"
//            keyPassword = "keyPassword"
//            storeFile =  file("../common.jks")
//            storePassword ="storePassword"
//        }
//    }

    buildTypes {
        release {
//            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig=true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

//    android.applicationVariants.all {
//        outputs.all {
//            if (this is ApkVariantOutputImpl) {
//                val config = project.android.defaultConfig
//                val versionName = config.versionName
//                val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm")
//                val createTime = LocalDateTime.now().format(formatter)
//                this.outputFileName = "${rootProject.extra["appName"]}_${this.name}_${versionName}_$createTime.apk"
//            }
//        }
//    }

    defaultConfig {
        setProperty("archivesBaseName", "${rootProject.extra["appName"]}-v${versionName}-${SimpleDateFormat("yyyy.MMdd.HH.mm.ss").format(Date())}")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(project(":common_core"))
}

