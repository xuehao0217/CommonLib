package com.xueh.comm_core.base.mvvm.ibase

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.helper.launchSafety
import com.xueh.comm_core.net.BaseResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 15:24
 * 备注：
 */
//abstract class AbsViewModel :AndroidViewModel(Utils.getApp())
abstract class AbsViewModel : ViewModel() {
    open val apiException: MutableLiveData<Throwable> = MutableLiveData()
    open val apiLoading: MutableLiveData<Boolean> = MutableLiveData()

    open  var apiComposeLoading = mutableStateOf(false)

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected var jobs = hashMapOf<String, Job>()

    //开启协程 如果有上一个任务 则取消
    //key 协程的 key
    fun launchCancelLast(key: String, block: suspend CoroutineScope.() -> Unit) = run {
        jobs[key]?.let {
            it.cancel()
        }
        viewModelScope.launchSafety {
            block.invoke(this)
        }.apply {
            jobs.put(key, this)
        }
    }
}