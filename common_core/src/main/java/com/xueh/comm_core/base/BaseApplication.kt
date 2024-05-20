package com.xueh.comm_core.base

import android.app.Application
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.ProcessUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.xueh.comm_core.helper.hasNetWorkConection

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/27 13:40
 * 备注：
 */
open abstract class BaseApplication : Application() {
    abstract fun init()
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        if (ProcessUtils.getCurrentProcessName().equals(getPackageName())) {
            Logger.addLogAdapter(AndroidLogAdapter())
            init()
        }
    }
}