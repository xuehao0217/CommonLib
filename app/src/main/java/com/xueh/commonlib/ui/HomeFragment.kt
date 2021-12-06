package com.xueh.commonlib.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class HomeFragment : MVVMFragment<FragmentHomeBinding, HomeViewModel>() {


    override fun initListener() {
        with(binding) {
            tvGetDsl.setOnClickListener {
                launch {
                    viewModel.loadDslData()
//                    viewModel.loadFlow().collect {
//                        ToastUtils.showLong("${it.data.toString()}")
//                    }
                }
            }

            tvDownload.setOnClickListener {
                viewModel.loadLiveData().observe(this@HomeFragment, Observer {
                    when (it) {
//                        is LiveDataResult.Response -> {
//                            ToastUtils.showShort(it.response.data.toString())
//                        }
                    }
                })
            }
            tvGetClean.setOnClickListener {
                viewModel.downloadFile()
            }
        }
    }

    val URL =
        "https://lh3.googleusercontent.com/-vFBVjRp14wam3b974OJcM2jQzu7Z-WJ_cDv4hijwcUhtmvJGjHVowXtasz2214O3MSD82dWUA=w128-h128-e365-rj-sc0x00ffffff"

    override fun initView(savedInstanceState: Bundle?) {
        binding.rv
            .grid(4).addGridItemDecoration(15f, 10f)
//            .linear().addLinearItemDecoration(R.color.white, 3, 15f)
            .bindingData(
                ItemLayoutBinding::inflate,
                mutableListOf("1", "2", "3", "4", "1", "2", "3", "4")
            ) { vh, vb, s ->
                vb.tvItem.text = s
                when (vh.layoutPosition) {
                    0 -> {
                        vb.tvItem.setRoundBg(10, R.color.colorAccent)
                    }
                    1 -> {
                        vb.tvItem.setRoundLineBg(
                            10,
                            R.color.white,
                            R.color.colorAccent,
                        )
                    }
                    2 -> {
                        vb.ivImage.setBackgroundColor(getColor(R.color.colorAccent))
                        vb.ivImage.loadImg(URL)
                    }

                    3 -> {
                        vb.ivImage.setBackgroundColor(getColor(R.color.colorAccent))
                        vb.ivImage.loadCircleImg(URL)
                    }

                    4 -> {
                        vb.ivImage.setBackgroundColor(getColor(R.color.colorAccent))
                        vb.ivImage.loadCircleImg(URL, 5)
                    }
                }
            }
    }

    override fun initDataAfterView() {
    }

    override fun initLivedata(viewModel: HomeViewModel) {
        viewModel.progressLiveData.observe(this, {
            binding.tvDownloadProgress.text = "下载进度:${it.percent},下载速度:${it.speed} byte"
        })
        viewModel.banner.observe(this) {
            ToastUtils.showShort(it.toString())
        }
    }

}