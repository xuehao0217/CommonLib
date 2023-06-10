package com.xueh.comm_core.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.ProcessUtils
import com.fengchen.uistatus.UiStatusNetworkStatusProvider
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.xueh.comm_core.R
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
        registerActivityLifecycle()
        UiStatusNetworkStatusProvider.getInstance()
            .registerOnRequestNetworkStatusEvent { context -> hasNetWorkConection() }
        if (ProcessUtils.getCurrentProcessName().equals(getPackageName())) {
            init()
        }
    }

    private fun registerActivityLifecycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity.findViewById<View>(R.id.tb_title_bar) != null) {
                    (activity.findViewById<View>(R.id.tb_title_bar) as TitleBar).run {
                        setOnTitleBarListener(object : OnTitleBarListener {
                            override fun onLeftClick(v: View?) {
                                activity?.finish()
                            }

                            override fun onRightClick(v: View?) {
                            }

                            override fun onTitleClick(v: View?) {
                            }
                        })
                    }
                }
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }
}