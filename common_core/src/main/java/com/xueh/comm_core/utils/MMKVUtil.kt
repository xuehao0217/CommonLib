package com.xueh.comm_core.utils

import android.content.Context
import android.os.Parcelable
import com.blankj.utilcode.util.GsonUtils
import com.tencent.mmkv.MMKV

object MMKVUtil {
    private val mmkv by lazy {
        MMKV.defaultMMKV()
    }

    private fun <T> putValue(key: String, value: T) {
        when (value) {
            is String -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Boolean -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is Long -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is ByteArray -> mmkv.encode(key, value)
            is Parcelable -> mmkv.encode(key, value)
            else -> mmkv.encode(key, GsonUtils.toJson(key))
        }
    }


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
            else -> defaultValue  // 如果无法匹配类型，返回默认值
        }
    }

    @JvmStatic
    fun set(key: String, value: Any) {
        putValue(key, value)
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

    fun init(context: Context){
        MMKV.initialize(context)
    }
}