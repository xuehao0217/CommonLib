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
 * 玩 Android 等接口；响应体为 [BaseResult]，由 Retrofit `converter-kotlinx-serialization` + [com.xueh.comm_core.net.JsonManager] 反序列化。
 * 若后端 `data` 结构不稳定，可另建接口用 [kotlinx.serialization.json.JsonElement]（参见 [BestieApiService]）+ [com.xueh.comm_core.net.decodeSuccessData]。
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
    suspend fun getHome(@Path("page") page: Int): BaseResult<HomeEntity>

}