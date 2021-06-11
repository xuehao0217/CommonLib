package com.xueh.comm_core.net

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 创 建 人: xueh
 * 创建日期: 2020/4/23 15:11
 * 备注：
 */
class HeaderInterceptor : Interceptor {

    private var headers = hashMapOf<String, String>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        headers.forEach { (t, u) ->
            requestBuilder.addHeader(t, u)
        }
        return chain.proceed(requestBuilder.build())
    }

    fun put(key: String, value: String) {
        headers[key] = value
    }

    fun put(headers: HashMap<String, String>) {
        this.headers.putAll(headers)
    }

    fun clearHead() {
        headers.clear()
    }
}