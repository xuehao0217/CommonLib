package com.xueh.commonlib.entity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/27 15:30
 * 备注：
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
    val url: String
) {
    @Transient
    val isSelect = mutableStateOf(false)
}