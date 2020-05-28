package com.xueh.commonlib.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.HttpRequest
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 10:51
 * 备注：
 */
class HomeViewModel : BaseViewModel<RestApi>() {

    override fun initApi() = HttpRequest.getService(RestApi::class.java)

    val banner = MutableLiveData<List<BannerVO>?>()

    fun loadData() {
        apiDSL<BaseResult<List<BannerVO>>> {
            onRequest {
                api.bannerList3()
            }
            onResponse {
                banner.postValue(it.data)
            }
        }
    }


    fun loadLiveData()=apiLiveData {
        api.bannerList3()
    }
}
