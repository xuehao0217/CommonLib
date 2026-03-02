plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.kotlin.serialization)
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
//    kotlinOptions {
//        jvmTarget = "21"
//    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
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
    api(libs.androidx.lifecycle.process)
    //----------------Compose相关--------------------------
    api(platform(libs.androidx.compose.bom))
    api(libs.constraintlayout.compose)
    api(libs.androidx.ui)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.material.icons.extended)
    api(libs.androidx.material3)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling)
    api( libs.compose.animation)
    api(libs.androidx.activity.compose)
    api(libs.compose.paging)
    api(libs.accompanist.permissions)
    api(libs.compose.navigation)
    api(libs.compose.lifecycle.runtime)
    api(libs.compose.lifecycle.viewmodel)
    api(libs.compose.runtime.livedata)
    api(libs.composeViews)

    api(libs.coil)
    api(libs.coil.compose)
    api(libs.coil.compose.okhttp)
    api(libs.coil3.coil.svg)

    api(libs.any.pop.dialog.compose)    //https://github.com/TheMelody/AnyPopDialog-Compose
    api (libs.landscapist.glide)

    api(libs.compose.webview)

    api (libs.refresh)
    api (libs.refresh.indicator)

    api(libs.accompanist.placeholder)
//    api(libs.androidx.photopicker.compose)
    //----------------Kotlin相关--------------------------
    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)

    //----------------网络请求相关--------------------------
    api(libs.okhttp3.okhttp)
    api(libs.retrofit)
    api(libs.okhttp3.logging.interceptor)
    api(libs.logginginterceptor)

    api(libs.retrofit.converter.kotlinx.serialization)
    api(libs.kotlinx.serialization.json)

    //网络请求查看日志相关
    debugApi(libs.chucker)
    releaseApi(libs.chucker.no.op)

//    api(libs.converter.gson)
//    api(libs.cookieJar)
    //---------------工具类的库--------------------------
    debugApi (libs.squareup.leakcanary)
    api (libs.interval)
    api(libs.utilcodex){
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
    }
    api(libs.progressmanager)
    api(libs.xxPermissions)
    api(libs.logger)
    api(libs.mmkv)
    api(libs.agentweb)
    api(libs.lottie.compose)

    api(libs.okhttp.profiler)

    //--------------XML相关一些库--------------------------
    api(libs.material)
    api(libs.recyclerview)
    api(libs.github.glide)
    api(libs.immersionbar)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.viewpager2)
    api(libs.github.brvah)
    api(libs.easyNavigation)
    api(libs.eventbus)
//    api(libs.gson)
//    //捕获崩溃信息
//    debugApi(libs.spiderman) {
//        exclude(group = "androidx.appcompat")
//    }
//    api(libs.refresh.layout)
//    api(libs.refresh.header)
}

