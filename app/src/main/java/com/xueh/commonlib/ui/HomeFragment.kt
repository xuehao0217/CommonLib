package com.xueh.commonlib.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.mvvm.MVVMFragment
import com.xueh.comm_core.helper.*
import com.xueh.comm_core.net.coroutinedsl.LiveDataResult
import com.xueh.comm_core.weight.GridItemDecoration
import com.xueh.comm_core.weight.SpacesItemDecoration
import com.xueh.commonlib.R
import com.xueh.commonlib.databinding.FragmentHomeBinding
import com.xueh.commonlib.databinding.ItemLayoutBinding
import com.xueh.commonlib.ui.viewmodel.HomeViewModel


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

            tvDownload.setOnClickListener {
                viewModel.loadLiveData().observe(this@HomeFragment, Observer {
                    when (it) {
                        is LiveDataResult.Response -> {
                            ToastUtils.showShort(it.response.data.toString())
                        }
                    }
                })
            }
            tvGetClean.setOnClickListener {
                viewModel.downloadFile()
            }
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        binding.rv.grid(4)
            .linear()
            .addLinearItemDecoration(R.color.white, 3, 15f)
            .bindingData(
                ItemLayoutBinding::inflate,
                mutableListOf("1", "2", "3")
            ) { vh, vb, s ->
                vb.tvItem.setRoundBg(10, R.color.colorAccent, R.color.white)
            }
    }

    override fun initDataAfterView() {
    }

    override fun initLivedata(viewModel: HomeViewModel) {
        viewModel.progressLiveData.observe(this, {
            binding.tvDownloadProgress.text="下载进度:${it.percent},下载速度:${it.speed} byte"
        })
    }
}