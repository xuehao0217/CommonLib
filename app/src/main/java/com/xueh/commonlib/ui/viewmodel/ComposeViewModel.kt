package com.xueh.commonlib.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.helper.pager
import com.xueh.comm_core.net.HttpRequest
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ComposeViewModel : BaseViewModel<RestApi>() {
    override fun initApi() = HttpRequest.getService(RestApi::class.java)

    // val bannerDatas by viewModel.bannerLiveData.observeAsState()
    val bannerLiveData = MutableLiveData<List<BannerVO>>()

    var bannerMutableState by mutableStateOf<List<BannerVO>>(listOf())

    var bannerMutableStates = mutableListOf<BannerVO>()


    fun loadDsl() {
        apiDSL<List<BannerVO>> {
            onRequest {
                api.bannerList3().data
            }
            onResponse {
                bannerLiveData.postValue(it)
                (0..5).forEach { index ->
                    bannerMutableStates.addAll(it)
                }
                bannerMutableState = bannerMutableStates
            }
        }
    }

//    var msgDatas by mutableStateOf<Flow<PagingData<HomeEntity>>?>(null)
    fun getListDatas() = pager { api.getHome(it).data.datas }
}

