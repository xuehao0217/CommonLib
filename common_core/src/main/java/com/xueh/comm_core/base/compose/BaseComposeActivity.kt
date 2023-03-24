package com.xueh.comm_core.base.compose

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.xueh.comm_core.weight.ViewLoading

//ComponentActivity
//AppCompatActivity  可以解决弹窗问题
abstract class BaseComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller?.isAppearanceLightStatusBars = !(resources.configuration.uiMode == 0x21)
        initView()
    }

    abstract fun initView()
    override fun onDestroy() {
        super.onDestroy()
        val decorView = window.decorView
        ViewTreeLifecycleOwner.set(decorView, null)
        ViewTreeViewModelStoreOwner.set(decorView, null)
        decorView.setViewTreeSavedStateRegistryOwner(null)
    }


    protected fun showProgressDialog() {
        ViewLoading.show(this)
    }

    protected fun hideProgressDialog() {
        ViewLoading.dismiss(this)
    }
}












