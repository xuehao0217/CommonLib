package com.xueh.comm_core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.xueh.comm_core.helper.inflateBindingWithGeneric

/**
 * @author: xuehao create time: 2017/7/26 11:28
 * tag: class//
 * description:  三级统一结构basefragment
 */
abstract class BaseFragment<VB : ViewBinding> : BaseVisibilityFragment(), IBaseLogic {

    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBindingWithGeneric(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataBeforeView()
        initView(savedInstanceState)
        initDataAfterView()
        initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

