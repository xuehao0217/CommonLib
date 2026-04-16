plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)
}

android {
    namespace = "com.xueh.comm_core"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    testImplementation(libs.junit)

    // AndroidX：DataStore / Paging / KTX / Lifecycle / Exif
    api(libs.androidx.datastore)
    api(libs.androidx.paging.common)
    api(libs.androidx.paging.runtime)
    api(libs.androidx.core.ktx)
    api(libs.androidx.activity.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.process)
    api(libs.androidx.exifinterface)

    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)

    // Jetpack Compose（BOM + 暴露给 app 的 UI 栈）
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.material3)
    api(libs.androidx.material.icons.extended)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling)
    api(libs.androidx.ui.tooling.preview)
    api(libs.compose.animation)
    api(libs.androidx.activity.compose)
    api(libs.compose.paging)
    api(libs.compose.lifecycle.runtime)
    api(libs.compose.lifecycle.viewmodel)
    api(libs.constraintlayout.compose)
    api(libs.compose.views)
    // api(libs.androidx.photopicker.compose)

    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)

    api(libs.androidx.media3.exoplayer)
    api(libs.androidx.media3.exoplayer.hls)
    api(libs.androidx.media3.exoplayer.dash)
    api(libs.androidx.media3.ui.compose)
    api(libs.androidx.media3.ui.compose.material3)

    api(libs.coil)
    api(libs.coil.compose)
    api(libs.coil.compose.okhttp)
    api(libs.coil3.coil.svg)
    api(libs.lottie.compose)

    // Retrofit / OkHttp / kotlinx-serialization
    api(libs.okhttp3.okhttp)
    api(libs.okhttp3.logging.interceptor)
    api(libs.retrofit)
    api(libs.retrofit.converter.kotlinx.serialization)
    api(libs.kotlinx.serialization.json)
    api(libs.saf.logging.interceptor)
    api(libs.okhttp.profiler)
    debugApi(libs.chucker)
    releaseApi(libs.chucker.no.op)

    // 工具（api 随 AAR 暴露；debug 专用单独列出）
    api(libs.interval)
    api(libs.utilcodex) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
    }
    api(libs.progressmanager)
    api(libs.xx.permissions)
    api(libs.logger)
    api(libs.mmkv)
    api(libs.agentweb)
    debugApi(libs.squareup.leakcanary)

    // 仅本模块内部使用，不 api 给 app
    implementation(libs.any.pop.dialog.compose)
    implementation(libs.parkwoocheol.compose.webview.android)
    implementation(libs.refresh)
    implementation(libs.refresh.indicator)
}

