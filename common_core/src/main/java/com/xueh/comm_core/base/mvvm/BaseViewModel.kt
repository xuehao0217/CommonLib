package com.xueh.comm_core.base.mvvm

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.helper.loge
import com.xueh.comm_core.helper.yes
import kotlinx.coroutines.*


/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 11:56
 * 备注：
 */
abstract class BaseViewModel<E> : AbsViewModel() {
    protected val api by lazy {
        initApi()
    }

    protected abstract fun initApi(): E

    fun launchOnUI(showLoading: Boolean = true, block: suspend CoroutineScope.() -> Unit) {
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            ToastUtils.showShort(throwable.toString())
            showLoading.yes {
                VMStateLiveData.postError()
//            VMStateLiveData.postValueAndSuccess(false)
            }
            loge("BaseViewModel", "exceptionHandler---${throwable}")
        }
        viewModelScope.launch(exceptionHandler) {
            showLoading.yes {
                VMStateLiveData.postLoading()
//            VMStateLiveData.postValueAndSuccess(true)
            }
            block()
        }.apply {
            invokeOnCompletion {
                showLoading.yes {
                    VMStateLiveData.clearState()
//                VMStateLiveData.postValueAndSuccess(false)
                }
                it?.let {
                    loge("BaseViewModel", "invokeOnCompletion---${it}")
                }
            }
        }
    }

    suspend fun <T> launchOnIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block
        }
    }
}