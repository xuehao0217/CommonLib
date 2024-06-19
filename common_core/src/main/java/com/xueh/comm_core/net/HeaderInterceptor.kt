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
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .method(originalRequest.method, originalRequest.body)


        headers.forEach { (t, u) ->
            requestBuilder.addHeader(t, u)
        }


//        val iterator = headers.iterator()
//        while(iterator.hasNext()){
//            val item = iterator.next()
//            requestBuilder.addHeader(item.key, item.value)
//        }

        return chain.proceed(requestBuilder.build())


//        val requestBuilder = chain.request().newBuilder()
//        headers.forEach { (t, u) ->
//            requestBuilder.addHeader(t, u)
//        }
//        return chain.proceed(requestBuilder.build())
    }

    fun put(key: String, value: String) {
        headers[key] = value
    }

    fun put(headers: HashMap<String, String>) {
        this.headers.putAll(headers)
    }


    fun clearKey(key: String) {
        if (headers.containsKey(key)) {
            headers.remove(key)
        }
    }

    fun clearHead() {
        headers.clear()
    }
}