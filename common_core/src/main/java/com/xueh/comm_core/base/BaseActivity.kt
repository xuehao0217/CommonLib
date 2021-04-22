package com.xueh.comm_core.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.xueh.comm_core.helper.inflateBindingWithGeneric

/**
 * @author: xuehao create time: 2017/7/26 11:29
 * tag: class//
 * description:三级统一结构型baseActivity
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), IBaseLogic {
    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        super.onCreate(savedInstanceState)
        binding = inflateBindingWithGeneric(layoutInflater)
        setContentView(binding.root)
        initDataBeforeView()
        initView(savedInstanceState)
        initListener()
        initDataAfterView()
    }
}