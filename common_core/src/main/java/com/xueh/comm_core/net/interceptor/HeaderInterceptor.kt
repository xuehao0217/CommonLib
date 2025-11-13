package com.xueh.comm_core.net.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap

/**
 * 全局请求头拦截器
 * - 支持动态添加、删除请求头
 * - 默认添加 Content-Type / Accept
 */
class HeaderInterceptor : Interceptor {

    private val headers = ConcurrentHashMap<String, String>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .method(original.method, original.body)

        // 添加自定义 header
        headers.forEach { (key, value) ->
            requestBuilder.header(key, value)
        }

        return chain.proceed(requestBuilder.build())
    }

    /** 添加或更新单个 header */
    fun put(key: String, value: String) {
        headers[key] = value
    }

    /** 批量添加 header */
    fun put(headers: Map<String, String>) {
        this.headers.putAll(headers)
    }

    /** 移除指定 header */
    fun remove(key: String) {
        headers.remove(key)
    }

    /** 清空所有自定义 header */
    fun clear() {
        headers.clear()
    }

    /** 获取当前所有 header（只读） */
    fun all(): Map<String, String> = headers.toMap()
}
