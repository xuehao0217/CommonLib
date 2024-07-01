package com.xueh.comm_core.base.xml

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ImmersionBar
import com.xueh.comm_core.R
import com.xueh.comm_core.helper.xml.EventBusHelper
import com.xueh.comm_core.helper.xml.EventBusRegister
import com.xueh.comm_core.weight.xml.ViewLoading
import kotlinx.coroutines.*

/**
 * @author: xuehao create time: 2017/7/26 11:29 tag: class//
 * description:二级统一业务型baseActivity
 */
abstract class DActivity<VB : ViewBinding> : BaseActivity<VB>(), CoroutineScope by MainScope() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isImmersionBarEnabled()) {
            initImmersionBar()
        }
        if (isRegisterEventBus()||javaClass.isAnnotationPresent(EventBusRegister::class.java)) {
            EventBusHelper.register(this)
        }
    }


    override fun onDestroy() {
        cancel()
        super.onDestroy()
        //在BaseActivity里销毁
        if (isRegisterEventBus()||javaClass.isAnnotationPresent(EventBusRegister::class.java)){
            EventBusHelper.unregister(this)
        }
        mImmersionBar=null
    }

    protected fun showProgressDialog() {
        ViewLoading.show(this)
    }

    protected fun hideProgressDialog() {
        ViewLoading.dismiss(this)
    }

    protected fun startActivity(intent: Intent, isNet: Boolean = false) {
        if (isNet) {
            if (NetworkUtils.isConnected()) {
                startActivity(intent)
            } else {
                ToastUtils.showShort(getString(R.string.str_no_net_prompts))
            }
        } else {
            startActivity(intent)
        }

    }

    protected fun startActivity(clazz: Class<out Activity>, isNet: Boolean = false) {
        startActivity(Intent(this, clazz), isNet)
    }


    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    var mImmersionBar: ImmersionBar? = null
    protected fun initImmersionBar() {
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this).apply {
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
            statusBarDarkFont(true, 0.2f)
            autoStatusBarDarkModeEnable(true, 0.3f)
            autoDarkModeEnable(true, 0.2f)
            navigationBarEnable(false)
            init()
        }
    }

    protected open fun isRegisterEventBus() = false

    protected open fun isImmersionBarEnabled() = false
}
