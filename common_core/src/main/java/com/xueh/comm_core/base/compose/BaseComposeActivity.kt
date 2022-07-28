package com.xueh.comm_core.base.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.xueh.comm_core.base.IBaseLogic
import com.xueh.comm_core.utils.compose.hideIme
import com.xueh.comm_core.utils.compose.setAndroidNativeLightStatusBar
import com.xueh.comm_core.utils.compose.showIme
import com.xueh.comm_core.utils.compose.transparentStatusBar

//ComponentActivity
//AppCompatActivity
abstract class BaseComposeActivity : AppCompatActivity(), IBaseLogic {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
        initListener()
        initData()
    }
}





