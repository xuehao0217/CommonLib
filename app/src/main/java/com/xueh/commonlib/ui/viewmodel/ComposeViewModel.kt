package com.xueh.commonlib.ui.viewmodel

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.helper.BasePagingSource
import com.xueh.comm_core.net.HttpRequest
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO
import com.xueh.commonlib.entity.HomeEntity

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


    val datas = Pager(PagingConfig(pageSize = 20)) {
        object : BasePagingSource<HomeEntity.Data>() {
            override suspend fun getDataList(page: Int) = api.getHome(page).data.datas
        }
    }.flow.cachedIn(viewModelScope)
}

