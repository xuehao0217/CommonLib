package com.xueh.comm_core.base.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.xueh.comm_core.base.BaseVisibilityFragment


abstract class BaseComposeFragmen : BaseVisibilityFragment() {
    abstract fun setComposeView(composeView: ComposeView)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireActivity()).apply {
        setComposeView(this)
    }
}

