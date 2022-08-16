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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xueh.comm_core.base.compose.theme.CommonLibTheme
import com.xueh.comm_core.base.compose.theme.GrayAppAdapter
import com.xueh.comm_core.base.compose.theme.themeTypeState
import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
import com.xueh.comm_core.helper.ViewModelHelper
import com.xueh.comm_core.utils.compose.setAndroidNativeLightStatusBar
import com.xueh.comm_core.utils.compose.transparentStatusBar
import com.xueh.comm_core.weight.ViewLoading
import com.xueh.comm_core.weight.compose.ComposeTitleView


abstract class MVVMComposeActivity<VM : AbsViewModel> : BaseComposeActivity {
    lateinit var viewModel: VM

    constructor() : super()

    override fun initListener() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        setAndroidNativeLightStatusBar()
    }

    override fun initView(savedInstanceState: Bundle?) {
        viewModel = ViewModelHelper.getViewModel(this.javaClass, this)
        viewModel.apiLoading.observe(this) {
            it?.let {
                if (it) ViewLoading.show(this) else ViewLoading.dismiss(this)
            }
        }

        viewModel.apiException.observe(this) {
            Log.e("MVVMComposeActivity", "BaseViewModel--> $it")
        }
    }

    @Composable
    protected open fun contentRoot(
        component: @Composable (BoxScope.() -> Unit),
    ) {
        CommonLibTheme(themeTypeState.value) {
            rememberSystemUiController().run {
                setStatusBarColor(Color.Blue, false)
                setSystemBarsColor(Color.Red, false)
                setNavigationBarColor(Color.Yellow, false)
            }
            GrayAppAdapter {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
//                        .background(Color.White)
                ) {
                    if (!getTitleText().isEmpty()) {
                        ComposeTitleView(getTitleText(), {
                            this@MVVMComposeActivity.finish()
                        })
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
//                            .background(color = Color.White),
                    ) {
                        component.invoke(this)
                    }
                }
            }

        }
    }

    protected open fun getTitleText() = ""

}