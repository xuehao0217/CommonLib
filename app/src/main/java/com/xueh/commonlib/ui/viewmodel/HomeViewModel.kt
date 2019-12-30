package com.xueh.commonlib.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.net.ServiceGenerator
import com.xueh.commonlib.api.RestApi

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 10:51
 * 备注：
 */
class HomeViewModel : BaseViewModel<RestApi>() {
    override fun initApi() = ServiceGenerator.getService(RestApi::class.java)
    private val refreshTrigger = MutableLiveData<Boolean>()
    private val bannerList = Transformations.switchMap(refreshTrigger) {
        api.bannerList()
    }
    val banners = Transformations.map(bannerList) {
        it.data ?: ArrayList()
    }
    fun loadData() {
        refreshTrigger.value = true
    }
}