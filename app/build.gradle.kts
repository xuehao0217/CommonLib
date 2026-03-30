import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xueh.commonlib"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
//    kotlinOptions {
//        jvmTarget = "21"
//    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    base {
        archivesName = "${libs.versions.appName.get()}-v${libs.versions.versionName.get()}-${SimpleDateFormat("yyyy.MMdd.HH.mm.ss").format(Date())}"
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
//                this.outputFileName = "${libs.versions.appName.get()}_${this.name}_${versionName}_$createTime.apk"
//            }
//        }
//    }

    packaging {
        resources {
            pickFirsts += setOf(
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            )
        }
    }
}

dependencies {
    implementation(project(":common_core"))
    testImplementation(libs.junit)
}

