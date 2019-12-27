package com.xueh.commonlib

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.ToastUtils
import com.fengchen.uistatus.UiStatusManager
import com.fengchen.uistatus.UiStatusNetworkStatusProvider
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.helper.hasNetWorkConection
import com.xueh.comm_core.utils.CommonUtils
import com.xueh.comm_core.weight.commtitle.CommTitleView
import com.xueh.comm_core.weight.commtitle.OnTitleLeftListener

/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 12:52
 * 备注：
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        registerActivityLifecycle()
        initToast()
        initState()
    }

    private fun initState() {
        UiStatusManager.getInstance()
            .addUiStatusConfig(UiStatus.LOADING, R.layout.state_loading)
            .addUiStatusConfig(UiStatus.EMPTY, R.layout.state_empty)
            .addUiStatusConfig(UiStatus.NETWORK_ERROR, R.layout.state_net_error)
        
        UiStatusNetworkStatusProvider.getInstance()
            .registerOnRequestNetworkStatusEvent { context -> hasNetWorkConection() }
    }

    private fun registerActivityLifecycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity.findViewById<View>(R.id.commtitle) != null) {
                    (activity.findViewById<View>(R.id.commtitle) as CommTitleView).run {
                        onTitleBarListener = object : OnTitleLeftListener() {
                            override fun onLeftClick(v: View) {
                                activity.finish()
                            }
                        }
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

    private fun initToast() {
        ToastUtils.setGravity(Gravity.CENTER, 0, 0)
        ToastUtils.setBgColor(Color.parseColor("#a0000000"))
        ToastUtils.setMsgColor(CommonUtils.getColor(R.color.white))
    }
}