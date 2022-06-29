package com.xueh.comm_core.base.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.xueh.comm_core.base.IBaseLogic


abstract class BaseComposeActivity : ComponentActivity(), IBaseLogic {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
        initListener()
        initData()
    }
}





