package com.xueh.comm_core.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.ToastUtils
import com.sunlands.comm_core.R
import com.xueh.comm_core.helper.EventBusHelper
import com.xueh.comm_core.statemanager.loader.StateRepository
import com.xueh.comm_core.statemanager.manager.StateChanger
import com.xueh.comm_core.statemanager.manager.StateEventListener
import com.xueh.comm_core.statemanager.manager.StateManager
import com.xueh.comm_core.statemanager.state.StateProperty
import com.xueh.comm_core.utils.CommonUtils
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
 * tag: class//
 * description:  二级统一业务baseFragment
 */
abstract class DFragment : BaseFragment(), StateChanger, CoroutineScope by MainScope() {
    protected var mCompositeDisposable = CompositeDisposable()
    private lateinit var stateManager: StateManager
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //该方法与onDetach对应,只有当对象完全销毁时解除事件绑定！
        //在oncreate时还是会多次调用的
        EventBusHelper.register(this)
//        NetStatusHelper.getInstance().register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        stateManager = StateManager.newInstance(getActivity(), StateRepository(getActivity()))
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        stateManager.contentView = view
    }

    override fun onDetach() {
        cancel()
        super.onDetach()
        //在onDestroyView时与activity还没有解绑！
        EventBusHelper.unregister(this)
//        NetStatusHelper.getInstance().unregister(this)
        mCompositeDisposable.clear()
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 0, sticky = true)
    fun basegetEvent(a: String) {
    }

    override fun initDataBeforeView() {}

    protected fun showProgressDialog() {
        ViewLoading.show(getActivity())
    }

    protected fun hideProgressDialog() {
        ViewLoading.dismiss(getActivity())
    }

    protected fun startActivity(clazz: Class<out Activity>) {
        val intent = Intent(getActivity(), clazz)
        if (IntentUtils.isIntentAvailable(intent)) {
            startActivity(intent)
        }
    }

    protected fun startActivity(clazz: Class<out Activity>, isNet: Boolean) {
        if (isNet) {
            if (CommonUtils.hasNetWorkConection()) {
                startActivity(clazz)
            } else {
                ToastUtils.showShort(getString(R.string.str_no_net_prompts))
            }
        } else {
            startActivity(clazz)
        }
    }

    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }


    fun View.setOnClick(function: (((data: Any) -> Unit))) {
        addDisposable(RxBindingUtils.setViewClicks(this, function))
    }


    override fun setStateEventListener(listener: StateEventListener) {
        stateManager.setStateEventListener(listener)
    }

    override fun showState(state: String) = stateManager.showState(state)

    override fun showState(state: StateProperty) = stateManager.showState(state)

    override fun getState() = stateManager.state


}
