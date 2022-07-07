package buildsrc

import org.gradle.api.JavaVersion

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/7
 * 备注：
 */
object ProjectPluginConfig {
//        maven { url 'https://maven.aliyun.com/repository/google' }
//        maven { url 'https://maven.aliyun.com/repository/jcenter' }
//        maven { url 'http://maven.aliyun.com/nexus/content/groups/public' }


    const val build_gradle = "com.android.tools.build:gradle:7.2.1"
    const val kotlin_gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21"

    val java_version = JavaVersion.VERSION_1_8.toString()
}