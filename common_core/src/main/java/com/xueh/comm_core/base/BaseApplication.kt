package com.xueh.comm_core.base

import android.app.Application
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.ProcessUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.xueh.comm_core.base.compose.theme.AppThemePreferences
import com.xueh.comm_core.utils.DataStoreUtils
/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/27 13:40
 * 备注：
 */
abstract class BaseApplication : MultiDexApplication() {
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