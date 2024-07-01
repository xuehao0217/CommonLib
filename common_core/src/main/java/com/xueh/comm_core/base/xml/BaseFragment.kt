package com.xueh.comm_core.base.xml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.xueh.comm_core.helper.xml.ViewBindingUtil

/**
 * @author: xuehao create time: 2017/7/26 11:28
 * tag: class//
 * description:  三级统一结构basefragment
 */
abstract class BaseFragment<VB : ViewBinding> : BaseVisibilityFragment(), IBaseLogic {
    val binding :VB by lazy {
        ViewBindingUtil.inflateWithGeneric(this, layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initListener()
        initData()
    }
}

