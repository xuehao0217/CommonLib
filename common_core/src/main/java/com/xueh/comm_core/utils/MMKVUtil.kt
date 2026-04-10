package com.xueh.comm_core.utils

import android.content.Context
import android.os.Parcelable
import com.tencent.mmkv.MMKV
import com.xueh.comm_core.net.JsonManager
import kotlin.PublishedApi

/**
 * MMKV 键值存取封装：[set]/[get] 按运行时类型分发 encode/decode；支持 [Parcelable]。
 * 需存 [@Serializable][kotlinx.serialization.Serializable] 模型时使用 [setSerializable] / [getSerializable]（与 [JsonManager] 一致，不用 Gson）。
 */
object MMKVUtil {

    private val mmkvLazy = lazy { MMKV.defaultMMKV() }

    private val mmkv: MMKV get() = mmkvLazy.value

    /** public inline 仅能调用可见 API，故通过 internal + [PublishedApi] 暴露同一 [MMKV] 实例。 */
    @PublishedApi
    internal fun mmkvForInline(): MMKV = mmkvLazy.value

    @JvmStatic
    fun set(key: String, value: Any) {
        when (value) {
            is String -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Boolean -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is Long -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is ByteArray -> mmkv.encode(key, value)
            is Parcelable -> mmkv.encode(key, value)
            else -> throw IllegalArgumentException(
                "MMKVUtil.set 仅支持基础类型与 Parcelable；@Serializable 类型请使用 setSerializable(key, value)",
            )
        }
    }

    /**
     * 将 [T] 以 [JsonManager.default] 序列化为 JSON 字符串写入 MMKV（适用于 [@Serializable] 类型）。
     */
    inline fun <reified T> setSerializable(key: String, value: T) {
        mmkvForInline().encode(key, JsonManager.default.encodeToString(value))
    }

    /**
     * 读取由 [setSerializable] 写入的 JSON；解析失败或缺键时返回 [defaultValue]。
     */
    inline fun <reified T> getSerializable(key: String, defaultValue: T): T {
        val store = mmkvForInline()
        if (!store.containsKey(key)) return defaultValue
        val raw = store.decodeString(key, "") ?: return defaultValue
        if (raw.isEmpty()) return defaultValue
        return runCatching { JsonManager.default.decodeFromString<T>(raw) }.getOrElse { defaultValue }
    }

    /**
     * 各分支按 [defaultValue] 的运行时类型读取，返回类型与默认值一致；[as T] 由分支穷尽保证安全。
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> getValue(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> mmkv.decodeString(key, defaultValue) as T
            is Float -> mmkv.decodeFloat(key, defaultValue) as T
            is Boolean -> mmkv.decodeBool(key, defaultValue) as T
            is Int -> mmkv.decodeInt(key, defaultValue) as T
            is Long -> mmkv.decodeLong(key, defaultValue) as T
            is Double -> mmkv.decodeDouble(key, defaultValue) as T
            is ByteArray -> mmkv.decodeBytes(key, defaultValue) as T
            is Parcelable -> mmkv.decodeParcelable(key, defaultValue.javaClass) as T
            else -> defaultValue
        }
    }

    @JvmStatic
    fun <T> get(key: String, defaultValue: T): T {
        if (!mmkv.containsKey(key)) {
            return defaultValue
        }
        return getValue(key, defaultValue)
    }

    @JvmStatic
    fun <T : Parcelable> get(key: String, tClass: Class<T>): T? {
        return if (mmkv.containsKey(key)) {
            mmkv.decodeParcelable(key, tClass)
        } else {
            null
        }
    }

    @JvmStatic
    fun removeKey(key: String) {
        mmkv.removeValueForKey(key)
    }

    @JvmStatic
    fun clearAll() {
        mmkv.clearAll()
    }

    fun init(context: Context) {
        MMKV.initialize(context)
    }
}
