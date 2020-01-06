package com.xueh.commonlib.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.ServiceGenerator
import com.xueh.comm_core.net.coroutine.getNetData
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 10:51
 * 备注：
 */
class HomeViewModel : BaseViewModel<RestApi>() {
    override fun initApi() = ServiceGenerator.getService(RestApi::class.java)

    val banner = MutableLiveData<List<BannerVO>?>()

    fun loadData() {
        launchOnUI {
            //              banner.postValue(api.bannerList2().await().data)
            banner.postValue(api.bannerList3().data)
        }
    }

    fun loadData2() {
        getNetData<BaseResult<List<BannerVO>>> {
            apiDsl = api.bannerList2()
            onSuccess {
                banner.postValue(it?.data)
            }
        }
    }
}
