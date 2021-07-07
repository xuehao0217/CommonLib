package com.xueh.commonlib

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.fengchen.uistatus.UiStatusManager
import com.fengchen.uistatus.annotation.UiStatus
import com.hjq.gson.factory.GsonFactory
import com.xueh.comm_core.base.BaseApplication
import com.xueh.comm_core.net.HttpRequest
import me.jessyan.progressmanager.ProgressManager
import retrofit2.converter.gson.GsonConverterFactory


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 12:52
 * 备注：
 */
class MyApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        initState()
        initLog()
        HttpRequest.apply {
            setting {
                okHttp {
                    it.apply {
                        ProgressManager.getInstance().with(this)
                            .build()
                    }
                }
                retrofit {
                    it.apply {
                        addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
                    }
                }
            }

            setBaseUrl("https://www.wanandroid.com/")
            putHead(hashMapOf("name" to "xuehao"))
        }

    }

    private fun initLog() {
        LogUtils.getConfig()
            .setLog2FileSwitch(true)
            .setSaveDays(7)
            .setFilePrefix("log")
            .dir = PathUtils.getExternalAppCachePath()
    }

    private fun initState() {
        UiStatusManager.getInstance()
            .addUiStatusConfig(UiStatus.LOADING, R.layout.state_loading)
            .addUiStatusConfig(UiStatus.EMPTY, R.layout.state_empty)
            //添加R.id.tv_net_error 点击事件
            .addUiStatusConfig(
                UiStatus.NETWORK_ERROR,
                R.layout.state_net_error,
                R.id.tv_net_error,
                null
            )
    }
}