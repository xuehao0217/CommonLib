package com.xueh.comm_core.net

import com.blankj.utilcode.util.Utils
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.safframework.http.interceptor.AndroidLoggingInterceptor
import com.xueh.comm_core.BuildConfig
import com.xueh.comm_core.net.interceptor.HeaderInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

/**
 * 全局 Http 请求管理器
 *
 * 特性：
 * - 自动缓存 BaseUrl 对应的 Retrofit 实例
 * - 动态添加请求头
 * - 支持 DSL 扩展 OkHttp/Retrofit
 * - Debug 环境自动启用 Chucker + 日志打印
 */
object HttpRequest {

    /** 缓存域名对应的 Retrofit 实例 */
    private val retrofitCache = ConcurrentHashMap<String, Retrofit>()

    /** 默认 BaseUrl */
    private var baseUrl: String = ""

    /** 外部可自定义 OkHttp / Retrofit 构建逻辑 */
    private var dslConfig: (RequestDsl.() -> Unit)? = null

    // =============================================================================================
    // 初始化入口
    // =============================================================================================

    fun init(url: String, config: (RequestDsl.() -> Unit)? = null) {
        baseUrl = url
        dslConfig = config
    }

    // =============================================================================================
    // Retrofit Service 创建
    // =============================================================================================

    /** 获取默认 BaseUrl 的 API Service */
    fun <T> getService(service: Class<T>): T = getServiceByDomain(baseUrl, service)

    /** 按指定域名创建 API Service（支持多域名） */
    fun <T> getServiceByDomain(domain: String, service: Class<T>): T {
        val retrofit = retrofitCache[domain] ?: createRetrofit(domain).also {
            if (domain == baseUrl) retrofitCache[domain] = it
        }
        return retrofit.create(service)
    }

    // =============================================================================================
    // Header 管理
    // =============================================================================================

    private val headerInterceptor by lazy { HeaderInterceptor() }

    fun putHeader(headers: Map<String, String>) = headerInterceptor.put(headers)
    fun putHeader(key: String, value: String) = headerInterceptor.put(key, value)
    fun removeHeader(key: String) = headerInterceptor.remove(key)
    fun clearHeaders() = headerInterceptor.clear()
    fun clearCache() = retrofitCache.clear()

    // =============================================================================================
    // OkHttp 构建
    // =============================================================================================

    private const val TIMEOUT = 60L

    private fun createOkHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .cache(Cache(Utils.getApp().cacheDir, 10L * 1024 * 1024))
            .addInterceptor(headerInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    trustAllSsl()
                    enableDebugFeatures()
                }
            }
    }

    /** Debug 环境下启用：日志 + Chucker */
    private fun OkHttpClient.Builder.enableDebugFeatures() {
        addNetworkInterceptor(
            AndroidLoggingInterceptor.build(
                isDebug = true,
                hideVerticalLine = true,
                requestTag = "HTTP",
                responseTag = "HTTP"
            )
        )
        addInterceptor(createChuckerInterceptor())
    }

    /** Debug 模式下信任所有 SSL 证书（用于自签名或测试环境） */
    private fun OkHttpClient.Builder.trustAllSsl() {
        try {
            val sslContext = SSLContext.getInstance("SSL")
            val trustManager = object : javax.net.ssl.X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            }
            sslContext.init(null, arrayOf(trustManager), SecureRandom())
            sslSocketFactory(sslContext.socketFactory, trustManager)
            hostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            throw RuntimeException("SSL init failed", e)
        }
    }

    private fun createChuckerInterceptor() = ChuckerInterceptor.Builder(Utils.getApp())
        .collector(
            ChuckerCollector(
                context = Utils.getApp(),
                showNotification = true,
                retentionPeriod = RetentionManager.Period.ONE_HOUR
            )
        )
        .maxContentLength(250_000L)
        .redactHeaders("Auth-Token", "Bearer")
        .alwaysReadResponseBody(true)
        .build()

    // =============================================================================================
    // Retrofit 构建
    // =============================================================================================

    private fun createRetrofit(url: String): Retrofit {
        val dsl = dslConfig?.let { RequestDsl().apply(it) }

        val okHttpBuilder = dsl?.okHttpBuilder?.invoke(createOkHttpClient()) ?: createOkHttpClient()
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpBuilder.build())

        return dsl?.retrofitBuilder?.invoke(retrofitBuilder)?.build() ?: retrofitBuilder.build()
    }
}

// =============================================================================================
// DSL 扩展
// =============================================================================================

/**
 * 支持自定义 Retrofit / OkHttp 构建的 DSL
 */
class RequestDsl {
    internal var okHttpBuilder: ((OkHttpClient.Builder) -> OkHttpClient.Builder)? = null
    internal var retrofitBuilder: ((Retrofit.Builder) -> Retrofit.Builder)? = null

    infix fun okHttp(block: ((OkHttpClient.Builder) -> OkHttpClient.Builder)?) {
        okHttpBuilder = block
    }

    infix fun retrofit(block: ((Retrofit.Builder) -> Retrofit.Builder)?) {
        retrofitBuilder = block
    }
}
