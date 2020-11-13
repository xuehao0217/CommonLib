package com.xueh.commonlib.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.lifecycle.Observer
import com.xueh.comm_core.base.mvvm.MVVMFragment
import com.xueh.comm_core.helper.setRoundBg
import com.xueh.comm_core.net.coroutinedsl.LiveDataResult
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class HomeFragment : MVVMFragment<HomeViewModel>() {
    override fun initListener() {
        tv_get_dsl.setOnClickListener {
            viewModel.loadData()
        }
        tv_get_other.setOnClickListener {
            viewModel.loadLiveData().observe(this, Observer {
                when (it) {
                    is LiveDataResult.Response -> {
                        tv_home.text = it.response.data.toString()
                    }
                }
            })
        }
        tv_get_clean.setOnClickListener {
            tv_home.text=""
        }
        tv_home.setOnClickListener {
            if(activity?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                //切换竖屏
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }else{
                //切换横屏
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
        }
    }

    override fun getLayoutId() = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        tv_home.setRoundBg(10, R.color.colorAccent, R.color.white)
    }

    override fun initDataAfterView() {
    }

    override fun initViewModel(): HomeViewModel = getViewModel()

    override fun initLivedata(viewModel: HomeViewModel) {
        viewModel.banner.observe(this, Observer {
            tv_home.text = it.toString()
        })
    }
}