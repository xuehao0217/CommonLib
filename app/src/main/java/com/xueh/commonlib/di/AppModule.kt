package com.xueh.commonlib.di

import com.xueh.commonlib.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/31 18:47
 * 备注：
 */
val viewModelModule = module {
    viewModel { HomeViewModel() }
}