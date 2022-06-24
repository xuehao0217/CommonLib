package com.xueh.commonlib.entity

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/27 15:30
 * 备注：
 */
data class BannerVO(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String,
    var listData:List<BannerVO>
)