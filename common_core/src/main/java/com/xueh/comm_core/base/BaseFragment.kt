package com.xueh.comm_core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * @author: xuehao create time: 2017/7/26 11:28
 * tag: class//
 * description:  三级统一结构basefragment
 */
abstract class BaseFragment : Fragment(), IBaseLogic {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )=  inflater.inflate(getLayoutId(), container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        initDataBeforeView()
        initView(savedInstanceState)
        initDataAfterView()
        initListener()
    }
}