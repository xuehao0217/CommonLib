package com.xueh.comm_core.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ImmersionBar
import com.xueh.comm_core.R
import com.xueh.comm_core.helper.EventBusHelper
import com.xueh.comm_core.helper.EventBusRegister
import com.xueh.comm_core.helper.hasNetWorkConection
import com.xueh.comm_core.weight.ViewLoading
import kotlinx.coroutines.*


/**
 * tag: class//
 * description:  二级统一业务baseFragment
 */
abstract class DFragment<VB : ViewBinding> : BaseFragment<VB>(), CoroutineScope by MainScope() {


    override fun onAttach(context: Context) {
        super.onAttach(context)
        //该方法与onDetach对应,只有当对象完全销毁时解除事件绑定！
        //在oncreate时还是会多次调用的
        if (isRegisterEventBus()||javaClass.isAnnotationPresent(EventBusRegister::class.java)) {
            EventBusHelper.register(this)
        }
    }

    override fun onDetach() {
        cancel()
        super.onDetach()
        //在onDestroyView时与activity还没有解绑！
        if (isRegisterEventBus()||javaClass.isAnnotationPresent(EventBusRegister::class.java)){
            EventBusHelper.unregister(this)
        }
        mImmersionBar?.let {
            it == null
        }
    }

    protected fun showProgressDialog() {
        ViewLoading.show(activity)
    }

    protected fun hideProgressDialog() {
        ViewLoading.dismiss(activity)
    }

    protected fun startActivity(clazz: Class<out Activity>) {
        val intent = Intent(activity, clazz)
        if (IntentUtils.isIntentAvailable(intent)) {
            startActivity(intent)
        }
    }

    protected fun startActivity(clazz: Class<out Activity>, isNet: Boolean) {
        if (isNet) {
            if (hasNetWorkConection()) {
                startActivity(clazz)
            } else {
                ToastUtils.showShort(getString(R.string.str_no_net_prompts))
            }
        } else {
            startActivity(clazz)
        }
    }


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isImmersionBarEnabled()) {
            initImmersionBar()
        }
    }

    protected open fun isRegisterEventBus() = false
    protected open fun isImmersionBarEnabled() = false
}
