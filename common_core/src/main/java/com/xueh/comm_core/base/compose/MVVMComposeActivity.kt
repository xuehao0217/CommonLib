package com.xueh.comm_core.base.compose

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xueh.comm_core.base.compose.theme.CommonLibTheme
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.helper.ViewModelHelper
import com.xueh.comm_core.weight.compose.ComposeTitleView


abstract class MVVMComposeActivity<VM : AbsViewModel> : BaseComposeActivity() {
    lateinit var viewModel: VM

    override fun initListener() {
    }

    override fun initView(savedInstanceState: Bundle?) {
        viewModel = ViewModelHelper.getViewModel(this.javaClass, this)
        viewModel.apiLoading.observe(this) {
            it?.let {
//                if (it) showProgressDialog() else hideProgressDialog()
            }
        }

        viewModel.apiException.observe(this) {
            it?.let {
                Log.e("BaseViewModel--> ", it?.toString())
            }
        }
    }

    @Composable
    protected open fun contentRoot(
        component: @Composable (BoxScope.() -> Unit)
    ) {
        CommonLibTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                if (!getTitleText().isEmpty()) {
                    ComposeTitleView(getTitleText(), {
                        this@MVVMComposeActivity.finish()
                    })
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White),
                ) {
                    component.invoke(this)
                }
            }
        }
    }

    protected open fun getTitleText() = ""

}