package com.xueh.comm_core.net

import com.blankj.utilcode.util.Utils
import com.xueh.comm_core.BuildConfig
import me.jessyan.progressmanager.ProgressManager
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

/***************************************************************************************************
//动态配置OkHttp Retrofit

private var requestDSL: (RequestDsl.() -> Unit)? = null
fun setting(requestDSL: (RequestDsl.() -> Unit)? = null) {
this.requestDSL = requestDSL
}


private fun getRetrofit(base_url: String): Retrofit {
val dsl = if (requestDSL != null) RequestDsl().apply(requestDSL!!) else null
val finalOkHttpBuilder = dsl?.buidOkHttp?.invoke(HttpRequest.getOkHttp()) ?: HttpRequest.getOkHttp()
val retrofitBuilder = Retrofit.Builder()
.baseUrl(base_url)
.addConverterFactory(GsonConverterFactory.create())
.client(finalOkHttpBuilder.build())
val finalRetrofitBuilder = dsl?.buidRetrofit?.invoke(retrofitBuilder) ?: retrofitBuilder
return finalRetrofitBuilder.build()
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

 ****************************************************************************************************/
object HttpRequest {
    private const val TIME_CONNECT = 60L
    private val mServiceMap: MutableMap<String, Retrofit> = HashMap()
    lateinit var DOMAIN_BASE: String

    fun setBaseUrl(base_url: String) {
        DOMAIN_BASE = base_url
    }


    fun <T> getService(serviceClass: Class<T>): T {
        return getCustomService(DOMAIN_BASE, serviceClass)
    }


    /**
     * @param domain Retrofit的BaseUrl
     */
    fun <T> getCustomService(domain: String, serviceClass: Class<T>): T {
        var retrofit = mServiceMap[domain]
        if (retrofit == null) {
            retrofit = getRetrofit(domain)
            //只缓存最常用的
            if (DOMAIN_BASE == domain) {
                mServiceMap[domain] = retrofit
            }
        }
        return retrofit.create(serviceClass)
    }


//    private fun getRetrofit(base_url: String): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(base_url)
//            .client(
//                ProgressManager.getInstance().with(getOkHttp())
//                    .build()
//            )
//            .addConverterFactory(GsonConverterFactory.create()) //返回内容的转换器
//            .build()
//    }

    //******************************** 动态配置OkHttp Retrofit **************************************
    private var requestDSL: (RequestDsl.() -> Unit)? = null
    fun setting(requestDSL: (RequestDsl.() -> Unit)? = null) {
        this.requestDSL = requestDSL
    }


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
    //***************************************公用参数*****************************************

    private var headers = HeaderInterceptor()

    fun putHead(header: HashMap<String, String>) {
        headers.put(header)
    }

    fun clearHead() = headers.clearHead()

    //****************************************公用参数********************************************


    //***************************************OkHttp*****************************************

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

    //********************************************************************************
}


