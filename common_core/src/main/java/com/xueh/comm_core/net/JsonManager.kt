package com.xueh.comm_core.net


import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

object JsonManager {

    /**
     * 默认全局 Json 实例
     */
    val default: Json by lazy {
        createDefault()
    }

    /**
     * 创建默认 Json
     */
    private fun createDefault(): Json {
        return Json {
            ignoreUnknownKeys = true      // 忽略未知字段
            isLenient = true              // 宽松解析
            encodeDefaults = true         // 编码默认值
            explicitNulls = false         // 不输出 null
            coerceInputValues = true      // 自动修正错误类型
            allowStructuredMapKeys = true
        }
    }

}


//val user = jsonString.fromJson<User>()
//val json = user.toJson()

/**
 * 解析对象
 */
inline fun <reified T> String.fromJson(
    json: Json = JsonManager.default
): T {
    return json.decodeFromString(this)
}

/**
 * 转 JSON 字符串
 */
inline fun <reified T> T.toJson(
    json: Json = JsonManager.default
): String {
    return json.encodeToString(this)
}