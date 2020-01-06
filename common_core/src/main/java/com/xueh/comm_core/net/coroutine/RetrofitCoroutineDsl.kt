package com.xueh.comm_core.net.coroutine

import retrofit2.Call

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 11:50
 * 备注：
 */

class RetrofitCoroutineDsl<ResultType> {
    var apiDsl: (Call<ResultType>)? = null

    internal var onSuccess: ((ResultType?) -> Unit)? = null
        private set
    internal var onComplete: (() -> Unit)? = null
        private set
    internal var onFailed: ((error: String?, code: Int) -> Unit)? = null
        private set

    var showFailedMsg = false

    internal fun clean() {
        onSuccess = null
        onComplete = null
        onFailed = null
    }

    fun onSuccess(block: (ResultType?) -> Unit) {
        this.onSuccess = block
    }

    fun onComplete(block: () -> Unit) {
        this.onComplete = block
    }

    fun onFailed(block: (error: String?, code: Int) -> Unit) {
        this.onFailed = block
    }

}
