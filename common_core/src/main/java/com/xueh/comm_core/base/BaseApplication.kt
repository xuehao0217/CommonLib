package com.xueh.comm_core.base

import android.app.Application
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.ProcessUtils
import com.fengchen.uistatus.UiStatusNetworkStatusProvider
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
        UiStatusNetworkStatusProvider.getInstance()
            .registerOnRequestNetworkStatusEvent { context -> hasNetWorkConection() }
        if (ProcessUtils.getCurrentProcessName().equals(getPackageName())) {
            init()
        }
    }
}