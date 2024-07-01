package com.xueh.comm_core.base.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.xueh.comm_core.base.xml.BaseVisibilityFragment
import com.xueh.comm_core.weight.xml.ViewLoading


abstract class BaseComposeFragment : BaseVisibilityFragment() {
    @Composable
    abstract fun setComposeContent()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireActivity()).apply {
        setContent {
            setComposeContent()
        }
    }

    protected fun showLoading() {
        ViewLoading.show(activity)
    }

    protected fun hideLoading() {
        ViewLoading.dismiss(activity)
    }

}

