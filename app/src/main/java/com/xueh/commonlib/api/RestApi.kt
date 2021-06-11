package com.xueh.commonlib.api

import androidx.lifecycle.LiveData
import com.xueh.comm_core.net.BaseResult
import com.xueh.commonlib.entity.BannerVO
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/27 15:29
 * 备注：
 */
interface RestApi {
    @GET("banner/json")
    fun bannerList(): LiveData<BaseResult<List<BannerVO>>>

    @GET("banner/json")
    suspend fun bannerList3(): BaseResult<List<BannerVO>>


    @Multipart
    @POST("forum/post/upload")
    suspend fun uploadFiles(
        @Part file: MultipartBody.Part
    ): BaseResult<String>
}