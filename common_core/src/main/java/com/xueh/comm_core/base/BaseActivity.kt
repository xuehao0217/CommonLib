package com.xueh.comm_core.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity

/**
 * @author: xuehao create time: 2017/7/26 11:29
 * tag: class//
 * description:三级统一结构型baseActivity
 */
abstract class BaseActivity : AppCompatActivity(), IBaseLogic {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(getLayoutId())
        super.onCreate(savedInstanceState)
        initDataBeforeView()
        initView(savedInstanceState)
        initListener()
        initDataAfterView()
    }
}