package com.xueh.comm_core.base.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import com.xueh.comm_core.base.IBaseLogic
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.helper.ViewModelHelper
import com.xueh.comm_core.weight.compose.ComposeTitleView


abstract class BaseComposeActivity : ComponentActivity(), IBaseLogic {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBeforeView()
        initView(savedInstanceState)
        initListener()
        initDataAfterView()
    }
}


abstract class MVVMComposeActivity<VM : AbsViewModel> : BaseComposeActivity() {
    lateinit var viewModel: VM

    override fun initDataBeforeView() {
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

    override fun initListener() {
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    @Composable
    protected open fun contentRoot(
        component: @Composable (BoxScope.() -> Unit)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            ComposeTitleView(getTitleText(), {
                this@MVVMComposeActivity.finish()
            })
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Red)
            ) {
                component.invoke(this)
            }
        }
    }

    protected open fun getTitleText() = ""

}



