package com.xueh.comm_core.net.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap

/**
 * 创 建 人: xueh
 * 创建日期: 2020/4/23 15:11
 * 备注：
 */
class HeaderInterceptor : Interceptor {

    var headers = ConcurrentHashMap<String, String>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .method(originalRequest.method, originalRequest.body)

        headers.forEach { (t, u) ->
            requestBuilder.addHeader(t, u)
        }

        return chain.proceed(requestBuilder.build())
    }

    fun put(key: String, value: String) {
        headers[key] = value
    }

    fun put(headers: Map<String, String>) {
        this.headers.putAll(headers)
    }

    fun clearHead() {
        headers.clear()
    }

    fun clearKey(key: String) {
        headers.remove(key)
    }
}