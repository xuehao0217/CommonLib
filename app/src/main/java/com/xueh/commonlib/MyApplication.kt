package com.xueh.commonlib

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.Utils
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.just.agentweb.AgentWebConfig
import com.xueh.comm_core.base.BaseApplication
import com.xueh.comm_core.net.HttpRequest
import com.xueh.comm_core.utils.MMKVUtil
import me.jessyan.progressmanager.ProgressManager


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 12:52
 * 备注：
 */
class MyApplication : BaseApplication() {
    override fun init() {
        initLog()
        initNet()
        MMKVUtil.init(this)
        AgentWebConfig.clearDiskCache(Utils.getApp())
    }

    private fun initNet() {
        HttpRequest.init("https://www.wanandroid.com/"){
            okHttp {
                it.apply {
                    ProgressManager.getInstance().with(this)
                        .build()
                }
            }
//            retrofit {
//                it.apply {
//                        addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
//                }
//            }
        }
        HttpRequest.putHead("name","xh")
    }

    private fun initLog() {
        LogUtils.getConfig()
            .setLog2FileSwitch(true)
            .setSaveDays(7)
            .setFilePrefix("log")
            .dir = PathUtils.getExternalAppCachePath()
    }
}