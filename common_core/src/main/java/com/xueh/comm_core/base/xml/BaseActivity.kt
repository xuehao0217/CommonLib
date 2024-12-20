package com.xueh.comm_core.base.xml

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.xueh.comm_core.helper.xml.ViewBindingUtil

/**
 * @author: xuehao create time: 2017/7/26 11:29
 * tag: class//
 * description:三级统一结构型baseActivity
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), IBaseLogic {

    val binding :VB by lazy {
        ViewBindingUtil.inflateWithGeneric(this, layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView(savedInstanceState)
        initListener()
        initData()
    }
}