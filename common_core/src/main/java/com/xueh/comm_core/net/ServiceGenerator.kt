package com.xueh.comm_core.net

import io.github.iamyours.wandroid.net.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

object ServiceGenerator {
    private val mServiceMap: MutableMap<String, Retrofit> = HashMap()
    private var DOMAIN_BASE=""
    fun setBaeUrl(httpBaseUrl: IHttpBaseUrl) {
        DOMAIN_BASE = httpBaseUrl.getBaseUrl()
    }

    fun <T> getService(serviceClass: Class<T>): T {
        return getCustomService(DOMAIN_BASE, serviceClass)
    }

    /**
     * @param domain Retrofit的BaseUrl
     */
    fun <T> getCustomService(domain: String, serviceClass: Class<T>): T {
        synchronized(ServiceGenerator::class.java) {
            var retrofit = mServiceMap[domain]
            if (retrofit == null) {
                retrofit = getRetrofit(domain)
                //只缓存最常用的
                if (DOMAIN_BASE == domain) {
                    mServiceMap[domain] = retrofit
                }
            }
            return createServiceFrom(retrofit, serviceClass)
        }
    }

    private fun <T> createServiceFrom(retrofit: Retrofit, serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    private fun getRetrofit(base_url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(base_url)
            .client(HttpUtils.getOkHttp()) //   .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()) //返回内容的转换器
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //请求Call的转换器
            .build()
    }

    fun reset() {
        mServiceMap.clear()
    }
}