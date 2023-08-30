package buildsrc

/**
 * 项目相关参数配置
 *
 */

object ProjectBuildConfig {
    const val compileSdkVersion = 34
    const val applicationId = "com.xueh.commonlib"
    const val appName = "comlib"
    const val minSdkVersion = 21
    const val targetSdkVersion = 34
    const val versionCode = 1
    const val versionName = "1.0.0"

    //可能切换为false时显示运行图标有缓存。切换之后需要clean rebuild，必要时可重启studio。
    const val isRunModule = false
}