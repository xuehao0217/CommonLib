package buildsrc


object Deps {
    object Version {
        const val compose = "1.2.0-rc02"
        const val kotlin = "1.6.21"
        const val coroutines = "1.6.3"

        const val core_ktx = "1.8.0"
        const val support = "28.0.0"
        const val constraint_layout = "2.1.4"
        const val appcompat = "1.4.2"
        const val androidx = "1.0.0"
        const val material = "1.6.1"
        const val lifecycle_version = "2.2.0"
        const val viewpager2 = "1.0.0"
        const val fragment_ktx = "1.4.1"
        const val recyclerview = "1.2.1"

        const val retrofit = "2.9.0"
        const val okhttp = "5.0.0-alpha.10"
        const val PersistentCookieJar = "v1.0.1"

        const val spiderman = "v1.1.8"
        const val coil = "2.1.0"
        const val coil_compose = "2.1.0"
        const val glide = "4.13.2"
        const val background = "1.7.4"
        const val progressmanager = "1.5.0"
        const val GsonFactory = "5.2"

        const val fastjson = "1.2.58"
        const val fastjsonandroid = "1.1.71"

        const val eventbus = "3.3.1"
        const val greendao = "3.3.0"
        const val greendaoupgrade = "v2.1.0"
        const val agentweb = "4.1.4"
        const val refresh_layout_kernel = "2.0.1"

        const val immersionbar = "3.0.0"
        const val utilcode = "1.31.0"
        const val permission = "2.0.3"
        const val BaseRecyclerViewAdapterHelper = "3.0.6"
        const val walle = "1.1.6"
        const val titlebar = "8.0"
        const val UiStatus = "1.0.5"
        const val MagicIndicator = "1.7.0"
        const val xpopup = "2.1.16"
        const val PickerView = "4.1.9"
        const val banner = "3.0.0"
    }

    object Androidx {
        const val junit = "junit:junit:4.13.2"
        const val material = "com.google.android.material:material:${Version.material}"
        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
        const val core_ktx = "androidx.core:core-ktx:${Version.core_ktx}"
        const val multidex = "androidx.multidex:multidex:2.0.1"
        const val support_v4 = "androidx.legacy:legacy-support-v4:${Version.androidx}"
        const val appcompat = "androidx.appcompat:appcompat:${Version.appcompat}"
        const val recyclerview = "androidx.recyclerview:recyclerview:${Version.recyclerview}"
        const val cardview = "androidx.cardview:cardview:${Version.androidx}"
        const val constraint_layout =
            "androidx.constraintlayout:constraintlayout:${Version.constraint_layout}"
        const val viewpager2 = "androidx.viewpager2:viewpager2:${Version.viewpager2}"
        const val fragment_ktx = "androidx.fragment:fragment-ktx:${Version.fragment_ktx}"
        const val lifecycle_common =
            "androidx.lifecycle:lifecycle-common-java8:${Version.lifecycle_version}"
        const val lifecycle_extensions =
            "androidx.lifecycle:lifecycle-extensions:${Version.lifecycle_version}"
        const val lifecycle_livedata_ktx =
            "androidx.lifecycle:lifecycle-livedata-ktx:${Version.lifecycle_version}"
        const val lifecycle_viewmodel_ktx =
            "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.lifecycle_version}"
    }

    object Github {
        const val spiderman = "com.github.simplepeng.SpiderMan:spiderman:${Version.spiderman}"
        const val ActivityResultLauncher = "com.github.DylanCaiCoding:ActivityResultLauncher:1.1.2"

        const val retrofit = "com.squareup.retrofit2:retrofit:${Version.retrofit}"
        const val converter_gson = "com.squareup.retrofit2:converter-gson:${Version.retrofit}"
        const val okhttp = "com.squareup.okhttp3:okhttp:${Version.okhttp}"
        const val okhttp_logging = "com.squareup.okhttp3:logging-interceptor:${Version.okhttp}"
        const val PersistentCookieJar =
            "com.github.franmontiel:PersistentCookieJar:${Version.PersistentCookieJar}"


        const val coil = "io.coil-kt:coil:${Version.coil}"
        const val coil_gif = "io.coil-kt:coil-gif:${Version.coil}"


        const val glide = "com.github.bumptech.glide:glide:${Version.glide}"
        const val progressmanager = "me.jessyan:progressmanager:${Version.progressmanager}"
        const val background =
            "com.github.JavaNoober.BackgroundLibrary:library:${Version.background}"
        const val GsonFactory = "com.github.getActivity:GsonFactory:${Version.GsonFactory}"
        const val gson = "com.google.code.gson:gson:2.8.7"


        const val eventbus = "org.greenrobot:eventbus:${Version.eventbus}"
        const val greendao = "org.greenrobot:greendao:${Version.greendao}"
        const val greendaoupgrade =
            "com.github.yuweiguocn:GreenDaoUpgradeHelper:${Version.greendaoupgrade}"


        const val fastjson = "com.alibaba:fastjson:${Version.fastjson}"
        const val fastjsonandroid = "com.alibaba:fastjson:${Version.fastjsonandroid}.android"
        const val agentweb = "com.just.agentweb:agentweb:${Version.agentweb}"
        const val refresh_layout_kernel =
            "com.scwang.smart:refresh-layout-kernel:${Version.refresh_layout_kernel}"
        const val refresh_header_classics =
            "com.scwang.smart:refresh-header-classics:${Version.refresh_layout_kernel}"
        const val immersionbar = "com.gyf.immersionbar:immersionbar:${Version.immersionbar}"
        const val utilcode = "com.blankj:utilcodex:${Version.utilcode}"
        const val AdapterHelper =
            "com.github.CymChad:BaseRecyclerViewAdapterHelper:${Version.BaseRecyclerViewAdapterHelper}"
        const val walle = "com.meituan.android.walle:library:${Version.walle}"
        const val banner = "cn.bingoogolapple:bga-banner:${Version.banner}@aar"
        const val UiStatus = "com.github.FengChenSunshine:UiStatus:${Version.UiStatus}"
        const val titlebar = "com.hjq:titlebar:${Version.titlebar}"
        const val MagicIndicator =
            "com.github.hackware1993:MagicIndicator:${Version.MagicIndicator}"
        const val xpopup = "com.lxj:xpopup:${Version.xpopup}"

        const val PickerView = "com.contrarywind:Android-PickerView:${Version.PickerView}"
    }


    object Compose {
        const val compose_ui = "androidx.compose.ui:ui:${Version.compose}"
        const val compose_material3 = "androidx.compose.material3:material3:1.0.0-alpha13"
        const val compose_preview =
            "androidx.compose.ui:ui-tooling-preview:${Version.compose}"
        const val compose_tooling = "androidx.compose.ui:ui-tooling:${Version.compose}"
        const val compose_manifest =
            "androidx.compose.ui:ui-test-manifest:${Version.compose}"
        const val activity_compose = "androidx.activity:activity-compose:1.4.0"
        const val compose_material = "androidx.compose.material:material:${Version.compose}"
        const val compose_livedata =
            "androidx.compose.runtime:runtime-livedata:${Version.compose}"
        const val compose_constraintlayout =
            "androidx.constraintlayout:constraintlayout-compose:1.0.1"
        const val coil_compose = "io.coil-kt:coil-compose:${Version.coil_compose}"
    }


    object Kotlin {
        const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}"

        const val kotlin_coroutines =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}"
        const val kotlin_coroutines_android =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutines}"
    }

}