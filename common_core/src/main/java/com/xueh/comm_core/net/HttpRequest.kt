package com.xueh.comm_core.net

import com.blankj.utilcode.util.Utils
import com.safframework.http.interceptor.AndroidLoggingInterceptor
import com.xueh.comm_core.BuildConfig
import com.xueh.comm_core.net.helper.XTrustManager
import com.xueh.comm_core.net.interceptor.HeaderInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

/***************************************************************************************************
https://github.com/RunFeifei/Run
 ****************************************************************************************************/
object HttpRequest {
    private val serviceMap= mutableMapOf<String, Retrofit>()
    lateinit var baseUrl: String
    fun init(url: String, requestDSL: (RequestDsl.() -> Unit)? = null) {
        this.baseUrl = url
        this.requestDSL = requestDSL
    }

    //************************************************************************************************

    fun <T> getService(serviceClass: Class<T>) = getCustomService(baseUrl, serviceClass)

    fun <T> getCustomService(domain: String, serviceClass: Class<T>): T {
        var retrofit = serviceMap[domain]
        if (retrofit == null) {
            retrofit = getRetrofit(domain)
            //只缓存最常用的
            if (baseUrl == domain) {
                serviceMap[domain] = retrofit
            }
        }
        return retrofit.create(serviceClass)
    }

    //***************************************公用参数*****************************************

    private var headers = HeaderInterceptor()

    fun putHead(header: HashMap<String, String>) {
        headers.put(header)
    }

    fun putHead(key: String, v: String) {
        clearService()
        headers.put(key, v)
    }

    fun clean(key: String) {
        headers.clearKey(key)
    }

    fun clearHead() = headers.clearHead()
    fun clearService() = serviceMap.clear()

    //****************************************公用参数********************************************


    //***************************************OkHttp*****************************************
    private const val TIME_CONNECT = 60L
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
                builder.hostnameVerifier { hostname, session ->
                    true
                }
                builder.addNetworkInterceptor(AndroidLoggingInterceptor.build(isDebug = BuildConfig.DEBUG, hideVerticalLine=true,requestTag="HTTP",responseTag="HTTP"))
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
    }

    //**********************************************************************************************


    //******************************** 动态配置OkHttp Retrofit **************************************
    private var requestDSL: (RequestDsl.() -> Unit)? = null
    private fun getRetrofit(base_url: String): Retrofit {
        val dsl = if (requestDSL != null) RequestDsl().apply(requestDSL!!) else null
        val finalOkHttpBuilder = dsl?.buidOkHttp?.invoke(getOkHttp()) ?: getOkHttp()
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(finalOkHttpBuilder.build())
        val finalRetrofitBuilder = dsl?.buidRetrofit?.invoke(retrofitBuilder) ?: retrofitBuilder
        return finalRetrofitBuilder.build()
    }
    //************************************************************************************************
}


class RequestDsl {
    internal var buidOkHttp: ((OkHttpClient.Builder) -> OkHttpClient.Builder)? = null

    internal var buidRetrofit: ((Retrofit.Builder) -> Retrofit.Builder)? = null

    infix fun okHttp(builder: ((OkHttpClient.Builder) -> OkHttpClient.Builder)?) {
        this.buidOkHttp = builder
    }

    infix fun retrofit(builder: ((Retrofit.Builder) -> Retrofit.Builder)?) {
        this.buidRetrofit = builder
    }
}