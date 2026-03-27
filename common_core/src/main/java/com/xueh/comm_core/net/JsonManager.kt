@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.xueh.comm_core.net

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Kotlin Serialization 的共享 [Json] 实例与扩展工具。
 *
 * **流程**：[HttpRequest] 构建 Retrofit 时使用 [default] 作为 Converter（与 Retrofit `converter-kotlinx-serialization` 一致，**不**使用 Gson）。
 *
 * 约定：`ignoreUnknownKeys`、**不编码默认值**（`encodeDefaults = false`）；JSON 键默认与 Kotlin 属性名一致（驼峰）。
 * 若后端使用蛇形等其它键名，在模型上用 [kotlinx.serialization.json.JsonNames] 或 [kotlinx.serialization.SerialName] 声明。
 */
object JsonManager {

    /**
     * 默认全局 Json 实例（见 [createDefault]）。
     */
    val default: Json by lazy {
        createDefault()
    }

    /**
     * 构建与 Retrofit Converter 共用的 [Json] 配置；下列项与 [kotlinx.serialization.json.Json] DSL 一一对应。
     */
    private fun createDefault(): Json {
        return Json {
            // 解码时忽略 JSON 里多出来的键，避免后端加字段导致反序列化失败（模型未声明的键直接丢弃）。
            ignoreUnknownKeys = true
            // 编码时跳过等于 Kotlin 默认值的属性，减小体积；解码侧仍可用 [coerceInputValues] 补默认。
            encodeDefaults = false
            // 允许非严格 JSON（如未加引号的键、单引号等），弱校验，便于对接不严谨的响应。
            isLenient = true
            // 为 false 时，非可空类型在 JSON 为 null 时不写入显式 null，减少与后端 null 策略的冲突（配合可选/默认）。
            explicitNulls = false
            // 若 JSON 缺少某键或值为 null，则对带默认值或非可空类型尝试填入默认值，减少「缺字段就崩」。
            coerceInputValues = true
            // 允许 Map 的键为对象/数组等结构化 JSON（默认仅支持字符串键）；一般用于特殊 JSON，开启后更宽松。
            allowStructuredMapKeys = true
        }
    }

}

/**
 * [BaseResult.data] 为 JSON 子树（[JsonElement]）时，在业务层解码为具体 [@Serializable] 类型。
 * 用于 Retrofit 对 `BaseResult<具体泛型>` 解析不稳定或后端 `data` 结构多变时的统一写法。
 */
inline fun <reified T> BaseResult<JsonElement?>.decodeSuccessData(): T {
    check(isSuccess()) { errorMsg }
    val element = data ?: error("data is null")
    return JsonManager.default.decodeFromJsonElement(element)
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