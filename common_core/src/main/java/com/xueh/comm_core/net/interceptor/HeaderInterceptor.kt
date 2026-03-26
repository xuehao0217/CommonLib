package com.xueh.comm_core.net.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap

/**
 * 全局请求头拦截器：在 [intercept] 中为每个请求合并默认 `Content-Type`/`Accept` 与 [put] 写入的动态头。
 *
 * **流程**：`HttpRequest` 构建 OkHttp 时注册本拦截器 → 业务通过 [HttpRequest.putHeader] 更新 [headers] → 后续请求自动携带。
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
