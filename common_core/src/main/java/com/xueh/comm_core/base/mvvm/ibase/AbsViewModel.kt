package com.xueh.comm_core.base.mvvm.ibase

import androidx.lifecycle.ViewModel
import com.xueh.comm_core.net.livedata.StateLiveData

/**
 * 创 建 人: xueh
 * 创建日期: 2019/12/30 15:24
 * 备注：
 */
//abstract class AbsViewModel :AndroidViewModel(Utils.getApp())
abstract class AbsViewModel : ViewModel() {
    val VMStateLiveData by lazy {
        StateLiveData<Boolean>()
    }
}