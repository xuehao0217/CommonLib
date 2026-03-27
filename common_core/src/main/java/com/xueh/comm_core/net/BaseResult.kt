package com.xueh.comm_core.net

import kotlinx.serialization.Serializable

/**
 * 与后端约定的通用包装：`errorCode == [STATE_SUCCESS]` 时 [isSuccess] 为 true，[data] 有效；否则用 [errorMsg] 展示或记入日志。
 *
 * **字段名与 JSON 键**：当前工程 [JsonManager] 未使用全局命名策略，JSON 键默认与 Kotlin 属性名一致（驼峰）。
 * 若后端键名不同，再在对应属性上使用下列注解（二者勿混用场景搞反）：
 *
 * **[kotlinx.serialization.SerialName]**
 * - 为属性指定 **唯一、固定的 JSON 主名**（通常一个字符串）。
 * - 编解码都围绕这个名字；适合后端 **只** 返回一种键名（例如永远是 `error_msg`）。
 *
 * **[kotlinx.serialization.json.JsonNames]**
 * - 声明 **多个可接受的 JSON 键名**（别名列表），解码时 **任一名称** 出现即可绑定到该属性。
 * - 适合兼容 **多种** 返回格式（例如既有 `errorMsg` 又有 `error_msg`）、迁移期或多环境。
 * - 属 JSON 模块能力，使用前需 `@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)`（文件级或类级）。
 *
 * **何时都不需要加**：JSON 键与属性名已一致（如 `errorCode` ↔ `errorCode`），保持模型简洁即可。
 */
@Serializable
data class BaseResult<T>(
    var errorCode: Int,
    var errorMsg: String,
    var data: T,
) {
    companion object {
        const val STATE_SUCCESS = 0
    }
    fun isSuccess() = errorCode == STATE_SUCCESS
}
