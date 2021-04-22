package com.xueh.commonlib.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.lifecycle.Observer
import com.xueh.comm_core.base.mvvm.MVVMFragment
import com.xueh.comm_core.helper.setRoundBg
import com.xueh.comm_core.net.coroutinedsl.LiveDataResult
import com.xueh.commonlib.R
import com.xueh.commonlib.databinding.FragmentHomeBinding
import com.xueh.commonlib.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class HomeFragment : MVVMFragment<FragmentHomeBinding, HomeViewModel>() {
    override fun initListener() {
        with(binding) {
            tvGetDsl.setOnClickListener {
                viewModel.loadData()
            }

            tvGetOther.setOnClickListener {
                viewModel.loadLiveData().observe(this@HomeFragment, Observer {
                    when (it) {
                        is LiveDataResult.Response -> {
                            tvHome.text = it.response.data.toString()
                        }
                    }
                })
            }
            tvGetClean.setOnClickListener {
                tvHome.text = ""
            }
            tvHome.setOnClickListener {
                if (activity?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //切换竖屏
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else {
                    //切换横屏
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                }
            }
        }


    }


    override fun initView(savedInstanceState: Bundle?) {
        binding.tvHome.setRoundBg(10, R.color.colorAccent, R.color.white)
    }

    override fun initDataAfterView() {
    }

    override fun initViewModel() = getViewModel<HomeViewModel>()

    override fun initLivedata(viewModel: HomeViewModel) {
        viewModel.banner.observe(this, Observer {
            binding.tvHome.text = it.toString()
        })
    }
}