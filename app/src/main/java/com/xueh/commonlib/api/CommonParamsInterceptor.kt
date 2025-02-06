package com.xueh.commonlib.api

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
///application/json 传递数据的公用参数
class CommonParamsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.body == null || originalRequest.header("Content-Type") != "application/json") {
            return chain.proceed(originalRequest)
        }

        val buffer = Buffer()
        originalRequest.body?.writeTo(buffer)
        val originalBodyString = buffer.readUtf8()

        val jsonObject = JSONObject(originalBodyString).apply {
            put("uniqueParam1", "uniqueValue1")
            put("uniqueParam2", "uniqueValue2")
        }

        val newRequestBody =  jsonObject.toString()
            .toRequestBody( "application/json; charset=utf-8".toMediaTypeOrNull())

        val newRequest = originalRequest.newBuilder()
            .method(originalRequest.method, newRequestBody)
            .build()

        return chain.proceed(newRequest)
    }
}

