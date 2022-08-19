//package com.xueh.comm_core.base.compose
//
//import android.os.Bundle
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxScope
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Divider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.constraintlayout.compose.ConstraintLayoutScope
//import com.xueh.comm_core.base.compose.theme.BaseComposeView
//import com.xueh.comm_core.base.compose.theme.GrayAppAdapter
//import com.xueh.comm_core.base.compose.theme.appThemeState
//import com.xueh.comm_core.base.mvvm.ibase.AbsViewModel
//import com.xueh.comm_core.helper.ViewModelHelper
//import com.xueh.comm_core.utils.compose.setAndroidNativeLightStatusBar
//import com.xueh.comm_core.utils.compose.setSystemBarsColor
//import com.xueh.comm_core.utils.compose.transparentStatusBar
//import com.xueh.comm_core.weight.ViewLoading
//import com.xueh.comm_core.weight.compose.CommonTitleView
//
//
//abstract class MVVMComposeActivity<VM : AbsViewModel> : BaseComposeActivity() {
//    lateinit var viewModel: VM
//
//
//    override fun initView(savedInstanceState: Bundle?) {
//        viewModel = ViewModelHelper.getViewModel(this.javaClass, this)
//        viewModel.apiLoading.observe(this) {
//            it?.let {
//                if (it) ViewLoading.show(this) else ViewLoading.dismiss(this)
//            }
//        }
//
//        viewModel.apiException.observe(this) {
//            Log.e("MVVMComposeActivity", "BaseViewModel--> $it")
//        }
//    }
//}