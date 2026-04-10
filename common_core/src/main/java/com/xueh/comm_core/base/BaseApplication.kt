package com.xueh.comm_core.base

import android.app.Application
import com.blankj.utilcode.util.ProcessUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.xueh.comm_core.base.compose.theme.AppThemePreferences
import com.xueh.comm_core.utils.DataStoreUtils

/**
 * 应用级 [Application]：主进程内完成通用初始化后再调用子类 [init]。
 *
 * **流程**（仅 `packageName` 进程）：
 * 1. 注册 Logger
 * 2. [DataStoreUtils.init] —— 供主题、业务键值等使用
 * 3. [AppThemePreferences.restore] —— 恢复亮暗色与主题色枚举
 * 4. [init] —— 子类实现：如 [HttpRequest.init]、推送、埋点等
 */
abstract class BaseApplication : Application() {
    abstract fun init()
    override fun onCreate() {
        super.onCreate()
        if (ProcessUtils.getCurrentProcessName().equals(getPackageName())) {
            Logger.addLogAdapter(AndroidLogAdapter())
            DataStoreUtils.init(this)
            AppThemePreferences.restore()
            init()
        }
    }
}