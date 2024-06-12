package com.xueh.commonlib.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.blankj.utilcode.util.ActivityUtils
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.helper.pager
import com.xueh.comm_core.net.HttpRequest
import com.xueh.comm_core.weight.ViewLoading
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO
import com.xueh.commonlib.entity.HomeEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class ComposeViewModel : BaseViewModel<RestApi>() {
    override fun initApi() = HttpRequest.getService(RestApi::class.java)

    // val bannerDatas by viewModel.bannerLiveData.observeAsState()
    val bannerLiveData = MutableLiveData<List<BannerVO>>()

    var bannerMutableState by mutableStateOf<List<BannerVO>>(listOf())

    fun loadDsl() {
        apiDSL<List<BannerVO>> {
            onRequest {
                api.bannerList3().data
            }
            onResponse {
                bannerLiveData.postValue(it)
            }
        }
    }



    var isFirst=true
    fun getListDatas() = pager { api.getHome(it).data.datas }


    var homeDatas by mutableStateOf<Flow<PagingData<HomeEntity.Data>>>(emptyFlow())

    var homeList by mutableStateOf<Flow<PagingData<HomeEntity.Data>>?>(null)
    fun getHomeDatas() {
        homeDatas= pager { api.getHome(it).data.datas }
    }

    fun getTestDatas() = pager {
        delay(2000)
        getTest(it)
    }.onStart {

    }

    fun getTest(index: Int): List<Int> {
        var list = mutableListOf<Int>()
        when (index) {
            1 -> {
                list.addAll((1..20))
            }
            2 -> {
                list.addAll((20..40))

            }
            3 -> {
                list
            }
        }
        return list
    }

}

