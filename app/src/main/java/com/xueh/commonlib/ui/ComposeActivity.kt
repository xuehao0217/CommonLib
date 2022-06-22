package com.xueh.commonlib.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.MVVMComposeActivity
import com.xueh.comm_core.weight.compose.ComposeTitleView
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.viewmodel.HomeViewModel

class ComposeActivity : MVVMComposeActivity<HomeViewModel>() {

    override fun initDataAfterView() {
        viewModel.banner.observe(this) {
            ToastUtils.showShort(it.toString())
        }
    }

    override fun initDataBeforeView() {
        super.initDataBeforeView()
        viewModel.loadDsl()
    }

    override fun getTitleText() = "我是标题"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        contentRoot {
            Text(text = "HHHHHHHHHHHH ")
        }
    }
}

