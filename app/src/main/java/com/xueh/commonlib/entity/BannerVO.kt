package com.xueh.commonlib.entity

import kotlinx.serialization.Serializable

/**
 * 轮播图项；JSON 键与 Kotlin 属性名一致（驼峰）。
 */
@Serializable
data class BannerVO(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String,
)
