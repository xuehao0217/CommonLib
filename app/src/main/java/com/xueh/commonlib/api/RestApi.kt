package com.xueh.commonlib.api

import androidx.lifecycle.LiveData
import com.xueh.comm_core.net.BaseResult
import com.xueh.commonlib.entity.BannerVO
import retrofit2.http.GET

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/27 15:29
 * 备注：
 */
interface RestApi{
    @GET("banner/json")
    fun bannerList(): LiveData<BaseResult<List<BannerVO>>>
}