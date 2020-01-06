package com.xueh.comm_core.net.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xueh.comm_core.helper.loge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 11:48
 * 备注：
 */

fun <ResultType> CoroutineScope.getNetData(
    dsl: RetrofitCoroutineDsl<ResultType>.() -> Unit //传递方法，需要哪个，传递哪个
) {
    this.launch(Dispatchers.Main) {
        val retrofitCoroutine = RetrofitCoroutineDsl<ResultType>()
        retrofitCoroutine.dsl()
        retrofitCoroutine.apiDsl?.let { it ->
            val work = async(Dispatchers.IO) {
                // io线程执行
                try {
                    it.execute()
                } catch (e: ConnectException) {
                    loge("HTTP", "CoroutineScope.getNetData-->${e}")
                    retrofitCoroutine.onFailed?.invoke("网络连接出错", -100)
                    null
                } catch (e: IOException) {
                    retrofitCoroutine.onFailed?.invoke("未知网络错误", -1)
                    null
                }
            }
            work.invokeOnCompletion { _ ->
                // 协程关闭时，取消任务
                if (work.isCancelled) {
                    it.cancel()
                    retrofitCoroutine.clean()
                }
            }
            val response = work.await()
            retrofitCoroutine.onComplete?.invoke()
            response?.let {
                if (response.isSuccessful) {
                    retrofitCoroutine.onSuccess?.invoke(response.body())
                } else {
                    // 处理 HTTP code
                    when (response.code()) {
                        401 -> {
                        }
                        500 -> {
                        }
                    }
                    retrofitCoroutine.onFailed?.invoke(
                        response.errorBody().toString(),
                        response.code()
                    )
                }
            }
        }
    }
}


fun <ResultType> ViewModel.getNetData(
    dsl: RetrofitCoroutineDsl<ResultType>.() -> Unit //传递方法，需要哪个，传递哪个
) {
    viewModelScope.launch {
        val retrofitCoroutine = RetrofitCoroutineDsl<ResultType>()
        retrofitCoroutine.dsl()
        retrofitCoroutine.apiDsl?.let { it ->
            val work = async(Dispatchers.IO) {
                // io线程执行
                try {
                    it.execute()
                } catch (e: ConnectException) {
                    loge("HTTP", "ViewModel.getNetData-->${e}")
                    retrofitCoroutine.onFailed?.invoke("网络连接出错", -100)
                    null
                } catch (e: IOException) {
                    retrofitCoroutine.onFailed?.invoke("未知网络错误", -1)
                    null
                }
            }
            work.invokeOnCompletion { _ ->
                // 协程关闭时，取消任务
                if (work.isCancelled) {
                    it.cancel()
                    retrofitCoroutine.clean()
                }
            }
            val response = work.await()
            retrofitCoroutine.onComplete?.invoke()
            response?.let {
                if (response.isSuccessful) {
                    retrofitCoroutine.onSuccess?.invoke(response.body())
                } else {
                    // 处理 HTTP code
                    when (response.code()) {
                        401 -> {
                        }
                        500 -> {
                        }
                    }
                    retrofitCoroutine.onFailed?.invoke(
                        response.errorBody().toString(),
                        response.code()
                    )
                }
            }
        }
    }
}