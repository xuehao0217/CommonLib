package com.xueh.comm_core.net

/**
 * 创 建 人: xueh
 * 创建日期: 2019/2/18 17:20
 * 备注：
 */
class BaseResult<T>(var err: Int, var msg: String, var data: T?) {
    companion object {
        const val STATE_SUCCESS = 0
    }
}