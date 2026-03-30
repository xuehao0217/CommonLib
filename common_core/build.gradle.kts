plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xueh.comm_core"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // 随 AAR 下发：app 开启 minify 时自动合并，无需在 app 重复抄写第三方与 common_core 网络栈规则
        consumerProguardFiles("proguard-rules.pro")
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
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //----------------基本库相关--------------------------
    api(libs.androidx.multidex)
    testImplementation(libs.junit)
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
    // 核心 Compose 库（需要暴露给 app 模块）
    api(platform(libs.androidx.compose.bom))
    api(libs.constraintlayout.compose)
    api(libs.androidx.ui)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.material.icons.extended)
    api(libs.androidx.material3)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling)
    api(libs.compose.animation)
    api(libs.androidx.activity.compose)
    api(libs.compose.paging)
    api(libs.compose.lifecycle.runtime)
    api(libs.compose.lifecycle.viewmodel)
    api(libs.compose.runtime.livedata)
    api(libs.composeViews)

    // 图片加载（Coil，app 模块也需要使用）
    api(libs.coil)
    api(libs.coil.compose)
    api(libs.coil.compose.okhttp)
    api(libs.coil3.coil.svg)
    api(libs.lottie.compose)

    // 仅 common_core 内部使用的 Compose 库
    implementation(libs.any.pop.dialog.compose)    //https://github.com/TheMelody/AnyPopDialog-Compose
    implementation(libs.parkwoocheol.compose.webview.android) // https://github.com/parkwoocheol/compose-webview
    implementation(libs.refresh)
    implementation(libs.refresh.indicator)

//    api(libs.androidx.photopicker.compose)

    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)
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

    api(libs.okhttp.profiler)
}

