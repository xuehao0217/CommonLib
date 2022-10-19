package com.xueh.commonlib.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.BaseComposeActivity
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
                viewModel.loadDsl()
            }

            tvDownload.setOnClickListener {
                viewModel.loadLiveData().observe(this@HomeFragment) {
                    when (it) {
                        is LiveDataResult.Response -> {
                            ToastUtils.showShort(it.response.data.toString())
                        }
                        is LiveDataResult.Error -> {

                        }
                        is LiveDataResult.Start -> {

                        }
                        is LiveDataResult.Finally -> {

                        }
                    }
                }
            }

            tvGetClean.setOnClickListener {
                viewModel.downloadFile()
            }
        }
    }

    val URL =
        "https://lh3.googleusercontent.com/-vFBVjRp14wam3b974OJcM2jQzu7Z-WJ_cDv4hijwcUhtmvJGjHVowXtasz2214O3MSD82dWUA=w128-h128-e365-rj-sc0x00ffffff"

    override fun initData() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val onBindAdapter = binding.rv
            .linear().addLinearItemDecoration(R.color.transparent,15)
//            .grid(4).addGridItemDecoration(15f, 10f)
            .onBindAdapter<ItemLayoutBinding, String> { item ->
                tvItem.text = item
            }.apply {
                setNewInstance(mutableListOf("Compose"))
                setOnItemClickListener { adapter, view, position ->
                    if (position==0){
                        startActivity(ComposeActivity::class.java)
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
        launch {
            viewModel.stateFlowDada.collect {
                ToastUtils.showShort(it.toString())
            }
        }
    }
}

