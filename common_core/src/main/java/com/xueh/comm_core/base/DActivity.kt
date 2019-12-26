package com.xueh.comm_core.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ToastUtils
import com.gyf.barlibrary.ImmersionBar
import com.noober.background.BackgroundLibrary
import com.sunlands.comm_core.R

import com.xueh.comm_core.helper.EventBusHelper
import com.xueh.comm_core.helper.hasNetWorkConection
import com.xueh.comm_core.statemanager.loader.StateRepository
import com.xueh.comm_core.statemanager.manager.StateChanger
import com.xueh.comm_core.statemanager.manager.StateEventListener
import com.xueh.comm_core.statemanager.manager.StateManager
import com.xueh.comm_core.statemanager.state.StateProperty
import com.xueh.comm_core.utils.rx.RxBindingUtils
import com.xueh.comm_core.weight.ViewLoading
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author: xuehao create time: 2017/7/26 11:29 tag: class//
 * description:二级统一业务型baseActivity
 */
abstract class DActivity : BaseActivity(), StateChanger,
    CoroutineScope by MainScope() {
    protected var mImmersionBar: ImmersionBar? = null
    protected var mCompositeDisposable = CompositeDisposable()
    lateinit var stateManager: StateManager
    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected val isImmersionBarEnabled = true

    public override fun onCreate(savedInstanceState: Bundle?) {
        BackgroundLibrary.inject(this)
        stateManager = StateManager.newInstance(this, StateRepository(this))
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
        stateManager.onDestoryView()
        EventBusHelper.unregister(this)
        mCompositeDisposable.clear()
        mImmersionBar?.let {
            it.destroy()
            it == null
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 0, sticky = true)
    fun basegetEvent(a: String) {
    }

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

    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }








    override fun setContentView(view: View) {
        super.setContentView(stateManager.setContentView(view))
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(stateManager.setContentView(layoutResID))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        super.setContentView(stateManager.setContentView(view), params)
    }

    override fun setStateEventListener(listener: StateEventListener) {
        stateManager.setStateEventListener(listener)
    }

    override fun showState(state: String) = stateManager.showState(state)

    override fun showState(state: StateProperty) = stateManager.showState(state)

    override fun getState() = stateManager.state
//    override fun onStart() {
//        super.onStart()
//        NetStatusHelper.getInstance().register(this)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        NetStatusHelper.getInstance().unregister(this)
//    }




    fun View.setOnClick(function: (((data: Any) -> Unit))) {
        addDisposable(RxBindingUtils.setViewClicks(this,function))
    }

}
