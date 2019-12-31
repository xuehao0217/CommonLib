package com.xueh.commonlib.ui.viewmodel

import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.net.ServiceGenerator
import com.xueh.comm_core.net.livedata.StateLiveData
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 10:51
 * 备注：
 */
class HomeViewModel : BaseViewModel<RestApi>() {
    override fun initApi() = ServiceGenerator.getService(RestApi::class.java)

    val stateBanner = StateLiveData<List<BannerVO>?>()

    fun loadData() {
        launchOnUI {
            //  stateBanner.postValueAndSuccess(api.bannerList2().await().data)
            stateBanner.postValueAndSuccess(api.bannerList3().data)
        }
    }
}
