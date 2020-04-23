package com.xueh.comm_core.net

import com.blankj.utilcode.util.Utils
import com.xueh.comm_core.BuildConfig
import io.github.iamyours.wandroid.net.LiveDataCallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

object HttpRequest {
    private val mServiceMap: MutableMap<String, Retrofit> = HashMap()
    private var DOMAIN_BASE=""

    fun setBaeUrl(httpBaseUrl: IHttpBaseUrl) {
        DOMAIN_BASE = httpBaseUrl.getHttpBaseUrl()
    }

    fun <T> getService(serviceClass: Class<T>): T {
        return getCustomService(DOMAIN_BASE, serviceClass)
    }

    /**
     * @param domain Retrofit的BaseUrl
     */
    fun <T> getCustomService(domain: String, serviceClass: Class<T>): T {
        synchronized(HttpRequest::class.java) {
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
            .client(getOkHttp().build()) //   .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()) //返回内容的转换器
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //请求Call的转换器
            .build()
    }

    fun reset() {
        mServiceMap.clear()
    }







    private var headers: HeaderInterceptor=HeaderInterceptor()
    private const val TIME_CONNECT = 60L


    fun putHead(key: String, value: String): HeaderInterceptor {
        headers.put(key, value)
        return headers
    }


    private fun getOkHttp(): OkHttpClient.Builder {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            try {
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(
                    null,
                    arrayOf(XTrustManager()),
                    SecureRandom()
                )
                val sslSocketFactory = sslContext.socketFactory
                builder.sslSocketFactory(sslSocketFactory, XTrustManager())
                builder.hostnameVerifier(HostnameVerifier { hostname, session ->
                    true
                })
                builder.addNetworkInterceptor(LoggingInterceptor())
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        return builder
            .cache(Cache(Utils.getApp().cacheDir, 10 * 1024 * 1024L))
            .addInterceptor(headers)
            .connectTimeout(TIME_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIME_CONNECT, TimeUnit.SECONDS)
            .writeTimeout(TIME_CONNECT, TimeUnit.SECONDS)
            .cookieJar(com.xueh.comm_core.net.cookie.CookieJar.getInstance())
    }
}