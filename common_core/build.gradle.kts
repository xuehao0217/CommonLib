import buildsrc.*

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.xueh.comm_core"
    compileSdk = ProjectBuildConfig.compileSdkVersion
    defaultConfig {
        minSdk = ProjectBuildConfig.minSdkVersion
        targetSdk =  ProjectBuildConfig.targetSdkVersion
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
        buildConfig=true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Deps.Version.compose_compiler
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)



    api(Deps.Androidx.junit)
    api(Deps.Androidx.espresso)
    api(Deps.Androidx.core_ktx)
    api(Deps.Androidx.multidex)
    api(Deps.Androidx.support_v4)
    api(Deps.Androidx.appcompat)
    api(Deps.Androidx.material)
    api(Deps.Androidx.recyclerview)
    api(Deps.Androidx.constraint_layout)
    api(Deps.Androidx.cardview)
    api(Deps.Androidx.viewpager2)
    api(Deps.Androidx.fragment_ktx)
    api(Deps.Androidx.lifecycle_common)
    api(Deps.Androidx.lifecycle_extensions)
    api(Deps.Androidx.lifecycle_livedata_ktx)
    api(Deps.Androidx.lifecycle_viewmodel_ktx)
    api(Deps.Androidx.datastore)
    api(Deps.Androidx.paging_runtime)
    api(Deps.Androidx.paging_common)
    api(Deps.Github.EasyNavigation)

    api(platform(Deps.Compose.compose_bom))
    androidTestImplementation(platform(Deps.Compose.compose_bom))

    api(Deps.Compose.compose_ui)
    api(Deps.Compose.compose_material3)
    api(Deps.Compose.compose_preview)
    api(Deps.Compose.compose_tooling)
    api(Deps.Compose.compose_manifest)
    api(Deps.Compose.compose_activity)
    api(Deps.Compose.compose_activity_ktx)
    api(Deps.Compose.compose_material)
    api(Deps.Compose.compose_livedata)
    api(Deps.Compose.coil_compose)
    api(Deps.Compose.compose_constraintlayout)
    api(Deps.Compose.compose_paging_compose)
    //api(Deps.Compose.accompanist_pager)
    api(Deps.Compose.accompanist_pager_indicators)
    api(Deps.Compose.accompanist_insets)
    api(Deps.Compose.accompanist_systemuicontroller)
    api(Deps.Compose.accompanist_swiperefresh)
    api(Deps.Compose.compose_smartrefresh)
    api(Deps.Compose.navigation_compose)
    api(Deps.Compose.lifecycle_runtime_ktx)
    api(Deps.Compose.lifecycle_viewmodel)
    api(Deps.Compose.accompanist_permissions)
    api(Deps.Compose.placeholder)

    debugImplementation(Deps.Compose.customview)
    debugImplementation(Deps.Compose.customview_poolingcontainer)

    api(Deps.Kotlin.kotlin_stdlib)
    api(Deps.Kotlin.kotlin_coroutines)
    api(Deps.Kotlin.kotlin_coroutines_android)

    api(Deps.Github.agentweb)
    api(Deps.Github.eventbus)
    api(Deps.Github.greendao)

    api(Deps.Github.okhttp_logging)
    api(Deps.Github.okhttp)
    api(Deps.Github.retrofit)
    api(Deps.Github.converter_gson)
    api(Deps.Github.PersistentCookieJar)

    debugImplementation(Deps.Github.spiderman) {
        exclude(group = "androidx.appcompat")
    }
    api(Deps.Github.coil)
    api(Deps.Github.coil_gif)
    api(Deps.Github.AdapterHelper)
    api(Deps.Github.glide)
    api(Deps.Github.progressmanager)
    api(Deps.Github.gson)
    api(Deps.Github.GsonFactory)

    api(Deps.Github.refresh_layout_kernel)
    api(Deps.Github.refresh_header_classics)

    api(Deps.Github.immersionbar)
    api(Deps.Github.utilcode)
    api(Deps.Github.MagicIndicator)
    api(Deps.Github.xpopup)
}

