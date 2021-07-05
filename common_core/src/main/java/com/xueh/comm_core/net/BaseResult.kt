package com.xueh.comm_core.net

/**
 * 创 建 人: xueh
 * 创建日期: 2019/2/18 17:20
 * 备注：
 */
data class BaseResult<T>(var errorCode: Int, var errorMsg: String, var data: T?) {
    companion object {
        const val STATE_SUCCESS = 0
    }
    fun isSuccess()=errorCode==STATE_SUCCESS
}
