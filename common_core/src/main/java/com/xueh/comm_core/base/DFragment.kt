package com.xueh.comm_core.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.ToastUtils
import com.fengchen.uistatus.UiStatusController
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.R
import com.xueh.comm_core.helper.EventBusHelper
import com.xueh.comm_core.helper.hasNetWorkConection
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
abstract class DFragment<VB : ViewBinding> : BaseFragment<VB>(), CoroutineScope by MainScope() {
    protected val mCompositeDisposable by lazy {
        CompositeDisposable()
    }
    protected val uiStatusController by lazy {
        UiStatusController.get()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //该方法与onDetach对应,只有当对象完全销毁时解除事件绑定！
        //在oncreate时还是会多次调用的
        EventBusHelper.register(this)
    }

    override fun onDetach() {
        cancel()
        super.onDetach()
        //在onDestroyView时与activity还没有解绑！
        EventBusHelper.unregister(this)
        mCompositeDisposable.clear()
    }


    @Subscribe(threadMode = ThreadMode.POSTING, priority = 0, sticky = true)
    fun basegetEvent(a: String) {
    }

    override fun initDataBeforeView() {

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

    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    fun View.setOnClick(function: (((data: Any) -> Unit))) {
        addDisposable(RxBindingUtils.setViewClicks(this, function))
    }

    fun bindStateView(view: View) = uiStatusController.bind(view)

    fun showState(@UiStatus state: Int) = uiStatusController.changeUiStatus(state)
}
