package com.xueh.commonlib.ui

import android.os.Bundle
import android.view.View
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.net.BaseResult
import com.xueh.comm_core.net.ServiceGenerator
import com.xueh.comm_core.net.coroutine.getNetData
import com.xueh.commonlib.R
import com.xueh.commonlib.api.RestApi
import com.xueh.commonlib.entity.BannerVO


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class MyFragment : DFragment() {
    override fun initListener() {
    }

    override fun getCreateViewLayoutId() = R.layout.fragment_my

    override fun initView(inflateView: View?, savedInstanceState: Bundle?) {
    }

    override fun initDataAfterView() {
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            getNetData<BaseResult<List<BannerVO>>> {
                api = ServiceGenerator.getService(RestApi::class.java).bannerList2()
                onSuccess {
                }
            }
        }
    }
}