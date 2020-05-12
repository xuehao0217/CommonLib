package com.xueh.comm_core.helper.activityresult

import android.app.Activity
import android.content.Intent

/**
 * 拦截器,通过ActivityResult启动activity之前会触发拦截器
 */
interface Intercept {
    /**
     * 拦截处理
     * @param activity
     * @param activityResult
     * @return 是否拦截  true 表示拦截不继续执行
     */
    fun onIntercept(
        activity: Activity?,
        activityResult: ActivityResult?
    ): Boolean
}


/**
 * Activity Result 结果回调
 */
interface ActivityResultListener {
    /**
     * 接收结果处理
     * @param resultCode
     * @param data 被打开Activity返回的结果数据
     */
    fun onReceiveResult(resultCode: Int, data: Intent?)
}