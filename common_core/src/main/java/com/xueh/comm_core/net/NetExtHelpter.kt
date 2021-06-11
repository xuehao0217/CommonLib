package com.xueh.comm_core.net

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * 创 建 人: xueh
 * 创建日期: 2021/6/11 14:54
 * 备注：
 */

//转换
fun File.getPart() = MultipartBody.Part.createFormData(
    "file",
    this.absolutePath,
    this.asRequestBody("image/jpeg".toMediaTypeOrNull())
)


