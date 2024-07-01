plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.xueh.comm_core"
    compileSdk = rootProject.extra["compileSdk"] as Int
    defaultConfig {
        minSdk =rootProject.extra["minSdk"] as Int
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["compose_compiler"] as String
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    //----------------基本库相关--------------------------
    api(libs.androidx.multidex)
    api(libs.junit)
    api(libs.androidx.appcompat)
    api(libs.androidx.datastore)
    api(libs.androidx.paging.common)
    api(libs.androidx.paging.runtime)
    //----------------KTX相关--------------------------
    api(libs.androidx.core.ktx)
    api(libs.androidx.activity.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.livedata.ktx)
    api(libs.androidx.lifecycle.viewmodel.ktx)//by viewModels()
    //----------------Compose相关--------------------------
    api(platform(libs.androidx.compose.bom))
    api(libs.constraintlayout.compose)
    api(libs.androidx.ui)
    api(libs.androidx.material3)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling)
    api(libs.androidx.activity.compose)
    api(libs.androidx.paging.compose)
    api(libs.accompanist.pager.indicators)
    api(libs.ccompanist)
    api(libs.accompanist.systemuicontroller)
    api(libs.accompanist.swiperefresh)
    api(libs.accompanist.permissions)
    api(libs.accompanist.placeholder)
    api(libs.navigation.compose)
    api(libs.lifecycle.runtime.compose)
    api(libs.lifecycle.viewmodel.compose)
    api( libs.compose.animation)
    api(libs.composeViews)

    api(libs.coil)
    api(libs.coil.gif)
    api(libs.coil.compose)
    api(libs.androidx.runtime.livedata)
    api(libs.composesmartrefresh)
    api(libs.any.pop.dialog.compose)    //https://github.com/TheMelody/AnyPopDialog-Compose

    //----------------Kotlin相关--------------------------
    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)

    //----------------网络请求相关--------------------------
    api(libs.retrofit)
    api(libs.converter.gson)
    api(libs.okhttp3.okhttp)
    api(libs.okhttp3.logging.interceptor)
    api(libs.cookieJar)

    //----------------网络请求查看日志相关--------------------------
    debugApi(libs.chucker)
    releaseApi(libs.chucker.no.op)

    //---------------工具类的库--------------------------
    api(libs.agentweb)
    api(libs.gson)
    api(libs.utilcodex)
    api(libs.progressmanager)
    api(libs.eventbus)
    api(libs.xxPermissions)
    api(libs.logger)
    //捕获崩溃信息
    debugApi(libs.spiderman) {
        exclude(group = "androidx.appcompat")
    }
    //--------------XML相关一些库--------------------------
    api(libs.material)
    api(libs.recyclerview)
    api(libs.github.glide)
    api(libs.immersionbar)
//    api(libs.refresh.layout)
//    api(libs.refresh.header)
//    api(libs.magicIndicator)
//    api(libs.xpopup)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.viewpager2)
    api(libs.github.brvah)
    api(libs.easyNavigation)
}

