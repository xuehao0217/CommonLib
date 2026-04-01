package com.xueh.commonlib.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xueh.comm_core.base.mvvm.BaseRequstViewModel
import com.xueh.comm_core.helper.compose.pager
import com.xueh.comm_core.net.HttpRequest
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO

class ComposeViewModel : BaseRequstViewModel<RestApi>() {
    override fun initApi() = HttpRequest.getService(RestApi::class.java)

    var bannerList by mutableStateOf<List<BannerVO>>(emptyList())
        private set

    fun loadBanner() = apiFlow(
        request = { api.bannerList3() },
        result = { bannerList = it }
    )

    fun getListDatas() = pager { page -> api.getHome(page).data.datas }
}
