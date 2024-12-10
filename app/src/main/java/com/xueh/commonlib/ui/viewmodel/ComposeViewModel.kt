package com.xueh.commonlib.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.xueh.comm_core.base.mvvm.BaseRequstViewModel
import com.xueh.comm_core.helper.compose.pager
import com.xueh.comm_core.net.HttpRequest
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO
import com.xueh.commonlib.entity.HomeEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onStart

class ComposeViewModel : BaseRequstViewModel<RestApi>() {
    override fun initApi() = HttpRequest.getService(RestApi::class.java)

    // val bannerDatas by viewModel.bannerLiveData.observeAsState()
    val bannerLiveData = MutableLiveData<List<BannerVO>>()

    var bannerMutableState by mutableStateOf<List<BannerVO>>(listOf())

    fun loadDsl() {
        apiDSL {
            onRequest {
                api.bannerList3().data
            }
            onResponse {
                bannerLiveData.postValue(it)
            }
        }
    }


    var showError = true
    fun getListDatas() = pager {pageNumber->
        // 当有过一次异常后，showError 将会是 false，避免后续出现异常
        try {
            when (pageNumber) {
                1 -> {
//                    throw Exception("模拟异常") // 你可以替换成具体网络调用
//                    emptyList<HomeEntity.Data>()
//                    // 处理可能的错误，这里可以通过 showError标志来控制
//                    if (showError) {
//                        // 用例：模拟第一次调用引发异常
//                        throw Exception("模拟异常") // 你可以替换成具体网络调用
//                    }else{
                        delay(3000)
                        api.getHome(pageNumber).data.datas
//                    }
                }

                4-> {
                      emptyList()
                }
                else -> {
                    // 处理可能的错误，这里可以通过 showError标志来控制
                    if (showError) {
                        // 用例：模拟第一次调用引发异常
                        throw Exception("模拟异常") // 你可以替换成具体网络调用
                    }else{
                        api.getHome(pageNumber).data.datas
                    }
                    // 获取数据
                }
            }
        } catch (e: Exception) {
            // 处理异常，当出现异常时，设置 showError 为 false
            showError = false
            // 可以在这里添加一些日志或反馈
            println("发生异常: ${e.message}") // 或者用 logger 打印
            throw Exception("模拟异常") // 你可以替
        }
    }


    var homeDatas by mutableStateOf<Flow<PagingData<HomeEntity.Data>>>(emptyFlow())

    var homeList by mutableStateOf<Flow<PagingData<HomeEntity.Data>>?>(null)
    fun getHomeDatas() {
        homeDatas = pager { api.getHome(it).data.datas }
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

