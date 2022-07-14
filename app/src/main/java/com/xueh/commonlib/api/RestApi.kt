package com.xueh.commonlib.api

import com.xueh.comm_core.net.BaseResult
import com.xueh.commonlib.entity.BannerVO
import com.xueh.commonlib.entity.HomeEntity
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/27 15:29
 * 备注：
 */
interface RestApi {

    @GET("banner/json")
    suspend fun bannerList3(): BaseResult<List<BannerVO>>


    @Multipart
    @POST("forum/post/upload")
    suspend fun uploadFiles(
        @Part file: MultipartBody.Part
    ): BaseResult<String>

    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>


    @GET("article/list/{page}/json")
    suspend fun getHome(@Path("page") int: Int): BaseResult<HomeEntity>

}