package com.xueh.comm_core.net

import kotlinx.serialization.Serializable

/**
 * 与后端约定的通用包装：`errorCode == [STATE_SUCCESS]` 时 [isSuccess] 为 true，[data] 有效；否则用 [errorMsg] 展示或记入日志。
 */
@Serializable
data class BaseResult<T>(var errorCode: Int, var errorMsg: String, var data: T) {
    companion object {
        const val STATE_SUCCESS = 0
    }
    fun isSuccess()=errorCode==STATE_SUCCESS
}
