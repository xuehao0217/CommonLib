package com.xueh.comm_core.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.fengchen.uistatus.UiStatusController
import com.fengchen.uistatus.annotation.UiStatus
import com.gyf.immersionbar.ImmersionBar
import com.noober.background.BackgroundLibrary
import com.xueh.comm_core.R
import com.xueh.comm_core.helper.EventBusHelper
import com.xueh.comm_core.helper.hasNetWorkConection
import com.xueh.comm_core.weight.ViewLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author: xuehao create time: 2017/7/26 11:29 tag: class//
 * description:二级统一业务型baseActivity
 */
abstract class DActivity<VB : ViewBinding> : BaseActivity<VB>(), CoroutineScope by MainScope() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        BackgroundLibrary.inject(this)
        super.onCreate(savedInstanceState)
        if (isImmersionBarEnabled) {
            initImmersionBar()
        }
        EventBusHelper.register(this)
    }

    override fun initDataBeforeView() {}

    override fun onDestroy() {
        cancel()
        super.onDestroy()
        //在BaseActivity里销毁
        EventBusHelper.unregister(this)
        mImmersionBar?.let {
            it == null
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 0, sticky = true)
    fun basegetEvent(a: String) {
    }

    protected fun showProgressDialog() {
        ViewLoading.show(this)
    }

    protected fun hideProgressDialog() {
        ViewLoading.dismiss(this)
    }

    protected fun startActivity(intent: Intent, isNet: Boolean = false) {
        if (isNet) {
            if (hasNetWorkConection()) {
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

    val uiStatusController by lazy {
        UiStatusController.get()
    }

    fun bindStateView(view: View) = uiStatusController.bind(view)

    fun showState(@UiStatus state: Int) = uiStatusController.changeUiStatusIgnore(state)


    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected val isImmersionBarEnabled = true
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
}
