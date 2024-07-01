package com.xueh.commonlib.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.chuckerteam.chucker.api.Chucker
import com.xueh.comm_core.base.mvvm.MVVMFragment
import com.xueh.comm_core.helper.*
import com.xueh.comm_core.net.HttpRequest
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
                viewModel.loadDsl()
            }

            tvDownload.setOnClickListener {
                viewModel.downloadFile()
            }

            tvGetClean.setOnClickListener {
                viewModel.loadFlow()
            }
        }
    }

    val URL = "https://lh3.googleusercontent.com/-vFBVjRp14wam3b974OJcM2jQzu7Z-WJ_cDv4hijwcUhtmvJGjHVowXtasz2214O3MSD82dWUA=w128-h128-e365-rj-sc0x00ffffff"

    override fun initData() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val onBindAdapter = binding.rv.linear().addLinearItemDecoration(R.color.transparent, 15)
//            .grid(4).addGridItemDecoration(15f, 10f)
            .onBindAdapter<ItemLayoutBinding, String> { item ->
                tvItem.text = item
            }.apply {
                setNewInstance(mutableListOf("查看网络日志"))
                setOnItemClickListener { adapter, view, position ->
                    when(position){
                        0->{
                            startActivity(Chucker.getLaunchIntent(requireContext()))
                        }
                        1->{

                        }
                    }

                }
            }
    }

    override fun initLiveData(viewModel: HomeViewModel) {
        viewModel.progressLiveData.observe(this) {
            binding.tvDownloadProgress.text = "下载进度:${it.percent},下载速度:${it.speed} byte"
        }
        viewModel.banner.observe(this) {
            ToastUtils.showShort(it.toString())
        }
        viewModel.stateFlowDada.collect(lifecycleScope){
            ToastUtils.showShort(it.toString())
        }
//        launch {
//            viewModel.stateFlowDada.collect {
//                ToastUtils.showShort(it.toString())
//            }
//        }
    }
}



