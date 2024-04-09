package com.xueh.comm_core.base.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.xueh.comm_core.base.BaseVisibilityFragment
import com.xueh.comm_core.base.compose.theme.appThemeState
import com.xueh.comm_core.weight.ViewLoading


abstract class BaseComposeFragment : BaseVisibilityFragment() {
    @Composable
    abstract fun setComposeContent()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireActivity()).apply {
        setContent {
            appThemeState = appThemeState.copy(
                darkTheme = isSystemInDarkTheme()
            )
            setComposeContent()
        }
    }

    protected fun showProgressDialog() {
        ViewLoading.show(activity)
    }

    protected fun hideProgressDialog() {
        ViewLoading.dismiss(activity)
    }

}

